package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.entry.domain.EntryDayProjector
import app.lade.entry.domain.EntryHistoryRepository
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.KindPriorityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

data class WeekDayRow(
	val date: LocalDate,
	val isToday: Boolean,
	val isSelected: Boolean,
	val dayBusy: CalendarDayBusy,
	val dueHabitsCount: Int,
	val previewTitles: List<String>,
	val previewOverflow: Int,
)

data class CalendarWeekUiState(
	val weekStart: LocalDate,
	val days: List<WeekDayRow>,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarWeekViewModel @Inject constructor(
	entryRepository: EntryRepository,
	entryHistoryRepository: EntryHistoryRepository,
	kindPriorityRepository: KindPriorityRepository,
	private val entryDayProjector: EntryDayProjector,
	private val dayPlanMapper: CalendarDayPlanMapper,
) : ViewModel() {
	private val weekFields = WeekFields.of(Locale.getDefault())
	private val anchorDate = MutableStateFlow(LocalDate.now())

	val selectedDate: StateFlow<LocalDate> = anchorDate

	val state: StateFlow<CalendarWeekUiState> = anchorDate.flatMapLatest { anchor ->
		val weekStart = anchor.with(weekFields.dayOfWeek(), 1L)
		val weekEnd = weekStart.plusDays(6)
		combine(
			entryRepository.observeActive(),
			entryHistoryRepository.observeBetween(weekStart, weekEnd),
			kindPriorityRepository.order,
		) { entries, histories, kindOrder ->
			val today = LocalDate.now()
			val plans = entryDayProjector.projectBetween(
				weekStart, weekEnd, entries, histories, kindOrder,
			)
			val days = (0L..6L).map { offset ->
				val date = weekStart.plusDays(offset)
				val plan = plans[date]
					?: entryDayProjector.project(date, entries, emptyList(), kindOrder)
				val dayBusy = dayPlanMapper.toDayBusy(plan)
				val habitSlots = dayPlanMapper.habitSlots(plan)
				val titles = buildPreviewTitles(dayBusy, habitSlots.map { it.title })
				WeekDayRow(
					date = date,
					isToday = date == today,
					isSelected = date == anchor,
					dayBusy = dayBusy,
					dueHabitsCount = habitSlots.size,
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
