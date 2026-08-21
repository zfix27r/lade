package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.habits.domain.DueHabitsProjector
import app.lade.habits.domain.HabitHistoryRepository
import app.lade.habits.domain.HabitRepository
import app.lade.habits.domain.model.HabitHistoryResult
import app.lade.time.domain.ExpandTimeSchedule
import app.lade.time.domain.TimeBlockRepository
import app.lade.time.domain.TimeScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

data class MonthDayCell(
	val date: LocalDate?,
	val isToday: Boolean,
	val hasBusy: Boolean,
	val hasHabits: Boolean,
	/** 0f..1f share of due habits marked done; 0 if none due. */
	val habitProgress: Float,
)

data class CalendarMonthUiState(
	val month: YearMonth,
	val weekdayLabels: List<DayOfWeek>,
	val days: List<MonthDayCell>,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarMonthViewModel @Inject constructor(
	scheduleRepository: TimeScheduleRepository,
	private val blockRepository: TimeBlockRepository,
	habitRepository: HabitRepository,
	historyRepository: HabitHistoryRepository,
	private val dueHabitsProjector: DueHabitsProjector,
	private val expandTimeSchedule: ExpandTimeSchedule,
) : ViewModel() {
	private val selectedMonth = MutableStateFlow(YearMonth.now())

	val month: StateFlow<YearMonth> = selectedMonth

	init {
		viewModelScope.launch {
			scheduleRepository.observeActive().collect { schedules ->
				expandTimeSchedule.syncAll(schedules)
			}
		}
	}

	val state: StateFlow<CalendarMonthUiState> = selectedMonth.flatMapLatest { yearMonth ->
		val from = yearMonth.atDay(1)
		val to = yearMonth.atEndOfMonth()
		combine(
			blockRepository.observeBetween(from, to),
			habitRepository.observeActive(),
			historyRepository.observeBetween(from.toEpochDay(), to.toEpochDay()),
		) { blocks, habits, histories ->
			val busyDays = blocks.map { it.date }.toSet()
			val historyByDayHabit = histories.associateBy { it.dateEpochDay to it.habitId }
			val progressByDay = HashMap<LocalDate, Float>()
			val habitDays = HashSet<LocalDate>()
			for (epoch in from.toEpochDay()..to.toEpochDay()) {
				val date = LocalDate.ofEpochDay(epoch)
				val due = dueHabitsProjector.project(date, habits)
				if (due.isEmpty()) continue
				habitDays += date
				val done = due.count { habit ->
					historyByDayHabit[epoch to habit.id]?.result == HabitHistoryResult.DONE
				}
				progressByDay[date] = done.toFloat() / due.size
			}
			buildMonthState(yearMonth, busyDays, habitDays, progressByDay)
		}
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		buildMonthState(YearMonth.now(), emptySet(), emptySet(), emptyMap()),
	)

	fun goPreviousMonth() {
		selectedMonth.value = selectedMonth.value.minusMonths(1)
	}

	fun goNextMonth() {
		selectedMonth.value = selectedMonth.value.plusMonths(1)
	}

	fun goCurrentMonth() {
		selectedMonth.value = YearMonth.now()
	}

	private fun buildMonthState(
		yearMonth: YearMonth,
		busyDays: Set<LocalDate>,
		habitDays: Set<LocalDate>,
		progressByDay: Map<LocalDate, Float>,
	): CalendarMonthUiState {
		val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
		val weekdays = (0..6).map { firstDayOfWeek.plus(it.toLong()) }
		val first = yearMonth.atDay(1)
		val lead = ((first.dayOfWeek.value - firstDayOfWeek.value + 7) % 7)
		val today = LocalDate.now()
		val cells = ArrayList<MonthDayCell>(42)
		repeat(lead) {
			cells += MonthDayCell(
				date = null,
				isToday = false,
				hasBusy = false,
				hasHabits = false,
				habitProgress = 0f,
			)
		}
		for (day in 1..yearMonth.lengthOfMonth()) {
			val date = yearMonth.atDay(day)
			cells += MonthDayCell(
				date = date,
				isToday = date == today,
				hasBusy = date in busyDays,
				hasHabits = date in habitDays,
				habitProgress = progressByDay[date] ?: 0f,
			)
		}
		while (cells.size % 7 != 0) {
			cells += MonthDayCell(
				date = null,
				isToday = false,
				hasBusy = false,
				hasHabits = false,
				habitProgress = 0f,
			)
		}
		return CalendarMonthUiState(
			month = yearMonth,
			weekdayLabels = weekdays,
			days = cells,
		)
	}
}
