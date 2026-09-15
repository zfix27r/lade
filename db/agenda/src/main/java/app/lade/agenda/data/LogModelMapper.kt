package app.lade.agenda.data

import app.lade.agenda.api.log.LogModel
import app.lade.entrystore.log.LogEntity

fun LogEntity.toDomain() = LogModel(
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
    createdAtEpochMs = createdAtEpochMs,
)

fun LogModel.toEntity() = LogEntity(
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
    createdAtEpochMs = createdAtEpochMs,
)