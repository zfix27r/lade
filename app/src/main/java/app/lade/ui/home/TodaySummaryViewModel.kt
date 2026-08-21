package app.lade.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.habits.domain.DueHabitsProjector
import app.lade.habits.domain.HabitHistoryRepository
import app.lade.habits.domain.HabitRepository
import app.lade.habits.domain.model.HabitHistoryResult
import app.lade.time.domain.DayBusyProjector
import app.lade.time.domain.ExpandTimeSchedule
import app.lade.time.domain.TimeBlockRepository
import app.lade.time.domain.TimeScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

data class TodaySummaryUiState(
	val free: Duration = Duration.ofHours(24),
	val dueTotal: Int = 0,
	val dueDone: Int = 0,
	val dueTitles: List<String> = emptyList(),
)

@HiltViewModel
class TodaySummaryViewModel @Inject constructor(
	scheduleRepository: TimeScheduleRepository,
	blockRepository: TimeBlockRepository,
	habitRepository: HabitRepository,
	historyRepository: HabitHistoryRepository,
	private val dayBusyProjector: DayBusyProjector,
	private val dueHabitsProjector: DueHabitsProjector,
	private val expandTimeSchedule: ExpandTimeSchedule,
) : ViewModel() {
	private val today = LocalDate.now()

	init {
		viewModelScope.launch {
			scheduleRepository.observeActive().collect { schedules ->
				expandTimeSchedule.syncAll(schedules)
			}
		}
	}

	val state: StateFlow<TodaySummaryUiState> = combine(
		blockRepository.observeByDate(today),
		habitRepository.observeActive(),
		historyRepository.observeByDate(today.toEpochDay()),
	) { blocks, habits, histories ->
		val dayBusy = dayBusyProjector.projectBlocks(today, blocks)
		val due = dueHabitsProjector.project(today, habits)
		val byHabit = histories.associateBy { it.habitId }
		val done = due.count { byHabit[it.id]?.result == HabitHistoryResult.DONE }
		TodaySummaryUiState(
			free = dayBusy.free,
			dueTotal = due.size,
			dueDone = done,
			dueTitles = due.take(3).map { it.title },
		)
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		TodaySummaryUiState(),
	)
}
