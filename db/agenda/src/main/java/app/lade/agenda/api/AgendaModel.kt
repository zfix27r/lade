package app.lade.agenda.api

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.log.LogModel

data class AgendaModel(
    val entry: EntryModel,
    val goals: List<GoalModel> = emptyList(),
    val logs: List<LogModel> = emptyList(),
)