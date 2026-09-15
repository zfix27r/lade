package app.lade.agenda.data.day

import app.lade.agenda.domain.models.DaySlot
import java.time.LocalDate

data class DayPlan(
    val date: LocalDate,
    val untimed: List<DaySlot>,
    val timed: List<DaySlot>,
)