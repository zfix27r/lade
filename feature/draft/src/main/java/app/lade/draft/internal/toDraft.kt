package app.lade.draft.internal

import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.goal.GoalUnit
import app.lade.draft.DraftGoal
import app.lade.draft.DraftModel
import app.lade.entrykind.EntryKind

internal fun AgendaModel.toDraft(): DraftModel = DraftModel(
    entryId = entry.id,
    title = entry.title,
    kind = entry.kind,
    dateFrom = entry.dateFrom,
    dateTo = entry.dateTo,
    timeFrom = entry.startTime,
    timeEnd = entry.endTime,
    rrule = entry.rrule,
    goals = goals.map { it.toDraftGoal() },
    alarms = emptyList(),
    reminders = emptyList(),
)

internal fun GoalModel.toDraftGoal(): DraftGoal = DraftGoal(
    id = id,
    title = title,
    unit = GoalUnit.fromStorage(unit),
    amount = amount,
    repeat = repeat,
    weight = weight,
)

internal fun DraftGoal.toGoalModel(entryId: Long): GoalModel = GoalModel(
    id = id,
    entryId = entryId,
    templateId = null,
    title = title,
    unit = unit.storage,
    amount = amount,
    repeat = repeat,
    weight = weight,
    archivedAtEpochMs = null,
)

internal fun DraftModel.toEntryModel(existing: EntryModel?): EntryModel {
    val now = System.currentTimeMillis()
    return EntryModel(
        id = entryId ?: 0L,
        kind = kind ?: EntryKind.TASK,
        title = title,
        templateId = existing?.templateId,
        dateFrom = dateFrom,
        dateTo = dateTo,
        startTime = timeFrom,
        endTime = timeEnd,
        rrule = rrule ?: existing?.rrule,
        alarmMode = existing?.alarmMode ?: "none",
        reminderMinutesBefore = existing?.reminderMinutesBefore,
        archivedAtEpochMs = existing?.archivedAtEpochMs,
        pausedAtEpochMs = existing?.pausedAtEpochMs,
        createdAtEpochMs = existing?.createdAtEpochMs ?: now,
        updatedAtEpochMs = if (existing != null) now else null,
    )
}