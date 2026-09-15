package app.lade.calendar.domain

import java.time.LocalDate

data class WeekDayRow(
    val date: LocalDate,
    val isToday: Boolean,
    val isSelected: Boolean,
    val dayBusy: CalendarDayBusy,
    val dueHabitsCount: Int,
    val previewTitles: List<String>,
    val previewOverflow: Int,
)
