package app.lade.calendar.domain

import app.lade.calendardata.api.CalendarCardModel
import app.lade.calendardata.api.DayProgress
import java.time.LocalDate
import java.time.YearMonth

data class CalendarStateModel(
    val mode: CalendarMode = CalendarMode.LIST,
    val currentDate: LocalDate = LocalDate.now(),
    val visibleMonth: YearMonth = YearMonth.from(currentDate),
    val entries: List<CalendarCardModel> = emptyList(),
    val markedDates: Map<LocalDate, DayProgress> = emptyMap(),
    val timelineDays: List<TimelineDay> = emptyList(),
    val selectedSources: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
)