package app.lade.agenda.data.goal

import app.lade.agenda.api.goal.GoalModel
import app.lade.agendastore.goal.GoalEntity

fun GoalEntity.toApi(): GoalModel = GoalModel(
    id = id,
    entryId = entryId,
    templateId = templateId,
    title = title,
    unit = unit,
    amount = amount,
    repeat = repeat,
    weight = weight,
    archivedAtEpochMs = archivedAtEpochMs,
)

fun GoalModel.toEntity(entryId: Long): GoalEntity = GoalEntity(
    id = id,
    entryId = entryId,
    templateId = templateId,
    title = title,
    unit = unit,
    amount = amount,
    repeat = repeat,
    weight = weight,
    archivedAtEpochMs = archivedAtEpochMs,
)