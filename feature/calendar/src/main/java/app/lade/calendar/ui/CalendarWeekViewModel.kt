package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.entry.EntryKind
import app.lade.calendar.domain.CalendarDayBusy
import app.lade.calendar.domain.CalendarWeekUiState
import app.lade.calendar.domain.WeekDayRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarWeekViewModel @Inject constructor(
	private val agendaApi: AgendaApi,
	private val dayPlanMapper: CalendarDayPlanMapper,
) : ViewModel() {
	private val weekFields = WeekFields.of(Locale.getDefault())
	private val anchorDate = MutableStateFlow(LocalDate.now())

	val selectedDate: StateFlow<LocalDate> = anchorDate

	val state: StateFlow<CalendarWeekUiState> = anchorDate.flatMapLatest { anchor ->
		val weekStart = anchor.with(weekFields.dayOfWeek(), 1L)
		val weekEnd = weekStart.plusDays(6)
		agendaApi.observeRange(weekStart, weekEnd).map { agendas ->
			val today = LocalDate.now()
			val byDate = agendas.groupBy { it.date }
			val days = (0L..6L).map { offset ->
				val date = weekStart.plusDays(offset)
				val dayAgendas = byDate[date].orEmpty()
				val dayBusy = dayPlanMapper.toDayBusy(date, dayAgendas)
				val habitTitles = dayAgendas
					.filter { it.entry.kind == EntryKind.HABIT }
					.map { it.entry.title }
				val titles = buildPreviewTitles(dayBusy, habitTitles)
				WeekDayRow(
					date = date,
					isToday = date == today,
					isSelected = date == anchor,
					dayBusy = dayBusy,
					dueHabitsCount = habitTitles.size,
					previewTitles = titles.titles,
					previewOverflow = titles.overflow,
				)
			}
			CalendarWeekUiState(weekStart = weekStart, days = days)
		}
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		emptyWeek(LocalDate.now()),
	)

	fun selectDate(date: LocalDate) {
		anchorDate.value = date
	}

	fun goPreviousWeek() {
		anchorDate.value = anchorDate.value.minusWeeks(1)
	}

	fun goNextWeek() {
		anchorDate.value = anchorDate.value.plusWeeks(1)
	}

	fun goToday() {
		anchorDate.value = LocalDate.now()
	}

	private fun emptyWeek(anchor: LocalDate): CalendarWeekUiState {
		val weekStart = anchor.with(weekFields.dayOfWeek(), 1L)
		val today = LocalDate.now()
		val emptyBusy = CalendarDayBusy(today, emptyList(), Duration.ZERO, Duration.ofHours(24))
		return CalendarWeekUiState(
			weekStart = weekStart,
			days = (0L..6L).map { offset ->
				val date = weekStart.plusDays(offset)
				WeekDayRow(
					date = date,
					isToday = date == today,
					isSelected = date == anchor,
					dayBusy = emptyBusy.copy(date = date),
					dueHabitsCount = 0,
					previewTitles = emptyList(),
					previewOverflow = 0,
				)
			},
		)
	}

	companion object {
		private const val PREVIEW_LIMIT = 2

		private data class Preview(
			val titles: List<String>,
			val overflow: Int,
		)

		private fun buildPreviewTitles(dayBusy: CalendarDayBusy, habitTitles: List<String>): Preview {
			val cleaned = dayBusy.intervals.map { it.title.trim() }.filter { it.isNotEmpty() } +
					habitTitles
			return Preview(
				titles = cleaned.take(PREVIEW_LIMIT),
				overflow = (cleaned.size - PREVIEW_LIMIT).coerceAtLeast(0),
			)
		}
	}
}