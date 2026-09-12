package app.lade.entry.domain

import java.time.LocalDate

data class DayPlan(
    val date: LocalDate,
    val untimed: List<DaySlot>,
    val timed: List<DaySlot>,
)