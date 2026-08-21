package app.lade.habits.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.habits.domain.DueHabitsProjector
import app.lade.habits.domain.HabitDayMarkKind
import app.lade.habits.domain.HabitHistoryRepository
import app.lade.habits.domain.HabitRepository
import app.lade.habits.domain.HabitStreakPreview
import app.lade.habits.domain.HabitStreakProjector
import app.lade.habits.domain.MarkHabitDay
import app.lade.habits.domain.model.Habit
import app.lade.habits.domain.model.HabitHistoryResult
import app.lade.habits.ui.mark.HabitMarkUndoEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class HabitListItem(
	val habit: Habit,
	val todayResult: String?,
	val dueToday: Boolean,
	val streak: HabitStreakPreview,
)

data class HabitHistorySheetState(
	val habit: Habit,
	val month: YearMonth,
	val marksByEpochDay: Map<Long, HabitDayMarkKind>,
)

@HiltViewModel
class HabitsListViewModel @Inject constructor(
	private val repository: HabitRepository,
	private val historyRepository: HabitHistoryRepository,
	private val markHabitDay: MarkHabitDay,
	private val dueHabitsProjector: DueHabitsProjector,
	private val streakProjector: HabitStreakProjector,
) : ViewModel() {
	private val today = LocalDate.now()
	private val historyFrom = today.minusDays(400)
	private val historyTo = today

	private val _undoEvents = MutableSharedFlow<HabitMarkUndoEvent>(extraBufferCapacity = 1)
	val undoEvents = _undoEvents.asSharedFlow()

	private val _historySheet = MutableStateFlow<HabitHistorySheetState?>(null)
	val historySheet: StateFlow<HabitHistorySheetState?> = _historySheet

	val items: StateFlow<List<HabitListItem>> = combine(
		repository.observeActive(),
		historyRepository.observeBetween(historyFrom.toEpochDay(), historyTo.toEpochDay()),
	) { habits, histories ->
		val todayHistories = histories.filter { it.dateEpochDay == today.toEpochDay() }
			.associateBy { it.habitId }
		val byHabit = histories.groupBy { it.habitId }
		habits.map { habit ->
			val habitHistories = byHabit[habit.id].orEmpty()
			HabitListItem(
				habit = habit,
				todayResult = todayHistories[habit.id]?.result,
				dueToday = dueHabitsProjector.project(today, listOf(habit)).isNotEmpty(),
				streak = streakProjector.preview(habit, today, habitHistories),
			)
		}
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	fun delete(id: Long) {
		viewModelScope.launch { repository.delete(id) }
	}

	fun markDone(habit: Habit) = mark(habit, HabitHistoryResult.DONE)

	fun markSkip(habit: Habit) = mark(habit, HabitHistoryResult.SKIPPED)

	fun undo(event: HabitMarkUndoEvent) {
		viewModelScope.launch {
			val habit = repository.getById(event.habitId) ?: return@launch
			markHabitDay.restore(habit, event.date, event.previousResult)
		}
	}

	fun openHistory(habit: Habit) {
		viewModelScope.launch {
			val month = YearMonth.from(today)
			val from = month.atDay(1)
			val to = month.atEndOfMonth()
			val marks = HashMap<Long, HabitDayMarkKind>()
			for (epoch in from.toEpochDay()..to.toEpochDay()) {
				val date = LocalDate.ofEpochDay(epoch)
				val history = historyRepository.get(habit.id, epoch)
				val due = dueHabitsProjector.project(date, listOf(habit)).isNotEmpty()
				marks[epoch] = when {
					!due -> HabitDayMarkKind.NONE
					history?.result == HabitHistoryResult.DONE -> HabitDayMarkKind.DONE
					history?.result == HabitHistoryResult.SKIPPED -> HabitDayMarkKind.SKIPPED
					date.isBefore(today) -> HabitDayMarkKind.MISSED
					else -> HabitDayMarkKind.PLANNED
				}
			}
			_historySheet.value = HabitHistorySheetState(
				habit = habit,
				month = month,
				marksByEpochDay = marks,
			)
		}
	}

	fun dismissHistory() {
		_historySheet.value = null
	}

	private fun mark(habit: Habit, result: String) {
		viewModelScope.launch {
			val previous = markHabitDay.mark(habit = habit, date = today, result = result)
			_undoEvents.emit(
				HabitMarkUndoEvent(
					habitId = habit.id,
					habitTitle = habit.title,
					dateEpochDay = today.toEpochDay(),
					newResult = result,
					previousResult = previous,
				),
			)
		}
	}
}
