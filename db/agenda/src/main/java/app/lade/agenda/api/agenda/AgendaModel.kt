package app.lade.agenda.api.agenda

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.log.LogModel
import java.time.LocalDate

data class AgendaModel(
    val date: LocalDate,
    val entry: EntryModel,
    val goals: List<GoalModel> = emptyList(),
    val logs: List<LogModel> = emptyList(),
    val fromSeries: Boolean = false,
)