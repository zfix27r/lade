package app.lade.agenda.data.log

import app.lade.agenda.api.log.LogModel
import app.lade.agenda.api.log.LogOrigin
import app.lade.agendastore.log.LogEntity

fun LogEntity.toApi(): LogModel = LogModel(
    id = id,
    entryId = entryId,
    epochDay = epochDay,
    goalId = goalId,
    name = name,
    unit = unit,
    plannedAmount = plannedAmount,
    plannedRepeat = plannedRepeat,
    plannedWeight = plannedWeight,
    actualAmount = actualAmount,
    actualRepeat = actualRepeat,
    actualWeight = actualWeight,
    origin = LogOrigin.fromStorage(origin),
    createdAtEpochMs = createdAtEpochMs,
)

fun LogModel.toEntity(): LogEntity = LogEntity(
    id = id,
    entryId = entryId,
    epochDay = epochDay,
    goalId = goalId,
    name = name,
    unit = unit,
    plannedAmount = plannedAmount,
    plannedRepeat = plannedRepeat,
    plannedWeight = plannedWeight,
    actualAmount = actualAmount,
    actualRepeat = actualRepeat,
    actualWeight = actualWeight,
    origin = origin.storage,
    createdAtEpochMs = createdAtEpochMs,
)