package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.categories.domain.CategoryRepository
import app.lade.habits.domain.DueHabitsProjector
import app.lade.habits.domain.HabitHistoryRepository
import app.lade.habits.domain.HabitRepository
import app.lade.habits.domain.MarkHabitDay
import app.lade.habits.domain.model.Habit
import app.lade.habits.domain.model.HabitHistoryResult
import app.lade.habits.ui.mark.HabitMarkUndoEvent
import app.lade.time.domain.DayBusy
import app.lade.time.domain.DayBusyProjector
import app.lade.time.domain.ExpandTimeSchedule
import app.lade.time.domain.TimeBlockRepository
import app.lade.time.domain.TimeScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DueHabitItem(
	val habit: Habit,
	val result: String?,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarDayViewModel @Inject constructor(
	scheduleRepository: TimeScheduleRepository,
	private val blockRepository: TimeBlockRepository,
	private val habitRepository: HabitRepository,
	historyRepository: HabitHistoryRepository,
	categoryRepository: CategoryRepository,
	private val dayBusyProjector: DayBusyProjector,
	private val dueHabitsProjector: DueHabitsProjector,
	private val markHabitDay: MarkHabitDay,
	private val expandTimeSchedule: ExpandTimeSchedule,
) : ViewModel() {
	private val selectedDate = MutableStateFlow(LocalDate.now())

	val date: StateFlow<LocalDate> = selectedDate

	private val _undoEvents = MutableSharedFlow<HabitMarkUndoEvent>(extraBufferCapacity = 1)
	val undoEvents = _undoEvents.asSharedFlow()

	val categoryColors: StateFlow<Map<Long, String>> = categoryRepository.observeActive()
		.map { categories -> categories.associate { it.id to it.color } }
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

	init {
		viewModelScope.launch {
			scheduleRepository.observeActive().collect { schedules ->
				expandTimeSchedule.syncAll(schedules)
			}
		}
	}

	val dayBusy: StateFlow<DayBusy> = selectedDate.flatMapLatest { date ->
		blockRepository.observeByDate(date).map { blocks ->
			dayBusyProjector.projectBlocks(date, blocks)
		}
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		dayBusyProjector.projectBlocks(LocalDate.now(), emptyList()),
	)

	private val historyForDate = selectedDate.flatMapLatest { date ->
		historyRepository.observeByDate(date.toEpochDay())
	}

	val dueHabits: StateFlow<List<DueHabitItem>> = combine(
		selectedDate,
		habitRepository.observeActive(),
		historyForDate,
	) { date, habits, histories ->
		val byHabit = histories.associateBy { it.habitId }
		dueHabitsProjector.project(date, habits).map { habit ->
			DueHabitItem(habit = habit, result = byHabit[habit.id]?.result)
		}
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		emptyList(),
	)

	fun selectDate(date: LocalDate) {
		selectedDate.value = date
	}

	fun goToday() {
		selectedDate.value = LocalDate.now()
	}

	fun goPreviousDay() {
		selectedDate.value = selectedDate.value.minusDays(1)
	}

	fun goNextDay() {
		selectedDate.value = selectedDate.value.plusDays(1)
	}

	fun markDone(habit: Habit) {
		mark(habit, HabitHistoryResult.DONE)
	}

	fun markSkip(habit: Habit) {
		mark(habit, HabitHistoryResult.SKIPPED)
	}

	fun undo(event: HabitMarkUndoEvent) {
		viewModelScope.launch {
			val habit = habitRepository.getById(event.habitId) ?: return@launch
			markHabitDay.restore(habit, event.date, event.previousResult)
		}
	}

	private fun mark(habit: Habit, result: String) {
		val date = selectedDate.value
		viewModelScope.launch {
			val previous = markHabitDay.mark(habit = habit, date = date, result = result)
			_undoEvents.emit(
				HabitMarkUndoEvent(
					habitId = habit.id,
					habitTitle = habit.title,
					dateEpochDay = date.toEpochDay(),
					newResult = result,
					previousResult = previous,
				),
			)
		}
	}
}
