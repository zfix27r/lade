package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.entry.EntryKind
import app.lade.calendar.domain.CalendarMonthUiState
import app.lade.calendar.domain.MonthDayCell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarMonthViewModel @Inject constructor(
	private val agendaApi: AgendaApi,
) : ViewModel() {
	private val selectedMonth = MutableStateFlow(YearMonth.now())

	val month: StateFlow<YearMonth> = selectedMonth

	val state: StateFlow<CalendarMonthUiState> = selectedMonth.flatMapLatest { yearMonth ->
		val from = yearMonth.atDay(1)
		val to = yearMonth.atEndOfMonth()
		agendaApi.observeRange(from, to).map { agendas ->
			val busyDays = HashSet<LocalDate>()
			val habitDays = HashSet<LocalDate>()
			val progressByDay = HashMap<LocalDate, Float>()

			agendas.groupBy { it.date }.forEach { (date, dayAgendas) ->
				val hasBusy = dayAgendas.any {
					it.entry.kind == EntryKind.SCHEDULE || it.entry.kind == EntryKind.EVENT
				}
				if (hasBusy) busyDays += date

				val habits = dayAgendas.filter { it.entry.kind == EntryKind.HABIT }
				if (habits.isNotEmpty()) {
					habitDays += date
					val done = habits.count { it.isDone() }
					progressByDay[date] = done.toFloat() / habits.size
				}
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

	fun selectMonth(month: YearMonth) {
		selectedMonth.value = month
	}

	private fun AgendaModel.isDone(): Boolean =
		logs.any { (it.actualAmount ?: 0) > 0 }

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