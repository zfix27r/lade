package app.lade.entrydetailsscreen.domain.model

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalModel

data class EntryEditData(
    val entry: EntryModel,
    val goals: List<GoalModel>,
)