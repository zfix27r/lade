package app.lade.calendar.internal.domain

import app.lade.calendar.internal.domain.mode.CalendarMode
import app.lade.calendar.internal.timeline.data.TimelineDay
import app.lade.calendardata.api.CalendarCardModel
import app.lade.calendardata.api.CalendarGoalExpandedModel
import app.lade.calendardata.api.DayProgress
import java.time.LocalDate

internal data class CalendarStateModel(
    val mode: CalendarMode = CalendarMode.LIST,
    val currentDate: LocalDate = LocalDate.now(),
    val entries: List<CalendarCardModel> = emptyList(),
    val markedDates: Map<LocalDate, DayProgress> = emptyMap(),
    val timelineDays: List<TimelineDay> = emptyList(),
    val selectedSources: Set<String> = emptySet(),
    val goalDetailsCache: Map<String, List<CalendarGoalExpandedModel>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
)