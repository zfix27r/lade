package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.habits.domain.DueHabitsProjector
import app.lade.habits.domain.HabitRepository
import app.lade.time.domain.DayBusy
import app.lade.time.domain.DayBusyProjector
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
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

data class WeekDayRow(
	val date: LocalDate,
	val isToday: Boolean,
	val isSelected: Boolean,
	val dayBusy: DayBusy,
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
	scheduleRepository: TimeScheduleRepository,
	private val blockRepository: TimeBlockRepository,
	habitRepository: HabitRepository,
	private val dayBusyProjector: DayBusyProjector,
	private val dueHabitsProjector: DueHabitsProjector,
	private val expandTimeSchedule: ExpandTimeSchedule,
) : ViewModel() {
	private val weekFields = WeekFields.of(Locale.getDefault())
	private val anchorDate = MutableStateFlow(LocalDate.now())

	val selectedDate: StateFlow<LocalDate> = anchorDate

	init {
		viewModelScope.launch {
			scheduleRepository.observeActive().collect { schedules ->
				expandTimeSchedule.syncAll(schedules)
			}
		}
	}

	val state: StateFlow<CalendarWeekUiState> = anchorDate.flatMapLatest { anchor ->
		val weekStart = anchor.with(weekFields.dayOfWeek(), 1L)
		val weekEnd = weekStart.plusDays(6)
		combine(
			blockRepository.observeBetween(weekStart, weekEnd),
			habitRepository.observeActive(),
		) { blocks, habits ->
			val today = LocalDate.now()
			val days = (0L..6L).map { offset ->
				val date = weekStart.plusDays(offset)
				val dayBlocks = blocks.filter { it.date == date }
				val dayBusy = dayBusyProjector.projectBlocks(date, dayBlocks)
				val dueHabits = dueHabitsProjector.project(date, habits)
				val titles = buildPreviewTitles(dayBusy, dueHabits.map { it.title })
				WeekDayRow(
					date = date,
					isToday = date == today,
					isSelected = date == anchor,
					dayBusy = dayBusy,
					dueHabitsCount = dueHabits.size,
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
		return CalendarWeekUiState(
			weekStart = weekStart,
			days = (0L..6L).map { offset ->
				val date = weekStart.plusDays(offset)
				WeekDayRow(
					date = date,
					isToday = date == today,
					isSelected = date == anchor,
					dayBusy = dayBusyProjector.projectBlocks(date, emptyList()),
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

		private fun buildPreviewTitles(dayBusy: DayBusy, habitTitles: List<String>): Preview {
			val cleaned = dayBusy.intervals.map { it.title.trim() }.filter { it.isNotEmpty() } +
				habitTitles
			return Preview(
				titles = cleaned.take(PREVIEW_LIMIT),
				overflow = (cleaned.size - PREVIEW_LIMIT).coerceAtLeast(0),
			)
		}
	}
}
