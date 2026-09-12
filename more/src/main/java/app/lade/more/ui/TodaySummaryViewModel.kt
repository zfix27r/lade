package app.lade.more.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.entry.domain.EntryDayProjector
import app.lade.entry.domain.EntryHistoryRepository
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.KindPriorityRepository
import app.lade.entry.data.EntryHistoryResult
import app.lade.entry.domain.models.EntryKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class TodaySummaryUiState(
	val openCount: Int = 0,
	val dueHabitTotal: Int = 0,
	val dueHabitDone: Int = 0,
	val previewTitles: List<String> = emptyList(),
)

@HiltViewModel
class TodaySummaryViewModel @Inject constructor(
	entryRepository: EntryRepository,
	entryHistoryRepository: EntryHistoryRepository,
	kindPriorityRepository: KindPriorityRepository,
	private val entryDayProjector: EntryDayProjector,
) : ViewModel() {
	private val today = LocalDate.now()

	val state: StateFlow<TodaySummaryUiState> = combine(
		entryRepository.observeActive(),
		entryHistoryRepository.observeByDate(today),
		kindPriorityRepository.order,
	) { entries, histories, kindOrder ->
		val plan = entryDayProjector.project(today, entries, histories, kindOrder)
		val openSlots = (plan.untimed + plan.timed).filter { slot ->
			when (slot.kind) {
				EntryKind.HABIT -> slot.result != EntryHistoryResult.DONE &&
					slot.result != EntryHistoryResult.SKIPPED
				EntryKind.TASK -> slot.result != EntryHistoryResult.DONE &&
					slot.result != EntryHistoryResult.CANCELLED
				EntryKind.EVENT, EntryKind.SCHEDULE -> true
			}
		}
		val habits = openSlots.filter { it.kind == EntryKind.HABIT } +
			(plan.untimed + plan.timed).filter {
				it.kind == EntryKind.HABIT && it.result == EntryHistoryResult.DONE
			}
		val habitDone = habits.count { it.result == EntryHistoryResult.DONE }
		TodaySummaryUiState(
			openCount = openSlots.size,
			dueHabitTotal = habits.size,
			dueHabitDone = habitDone,
			previewTitles = openSlots.take(3).map { it.title },
		)
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		TodaySummaryUiState(),
	)
}
