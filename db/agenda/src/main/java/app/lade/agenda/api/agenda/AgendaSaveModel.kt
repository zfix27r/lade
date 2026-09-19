package app.lade.agenda.api.agenda

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalModel

data class AgendaSaveModel(
    val entry: EntryModel,
    val goals: List<GoalModel> = emptyList(),
)