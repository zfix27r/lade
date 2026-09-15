package app.lade.calendar.domain

import java.time.LocalDate

data class MonthDayCell(
    val date: LocalDate?,
    val isToday: Boolean,
    val hasBusy: Boolean,
    val hasHabits: Boolean,
    val habitProgress: Float,
)
