package app.lade.agenda.data.entry

import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryModel
import app.lade.entrystore.entry.EntryEntity
import java.time.LocalDate
import java.time.LocalTime

fun EntryEntity.toApi(): EntryModel = EntryModel(
    id = id,
    kind = EntryKind.entries.find { it.storage == kind } ?: EntryKind.UNKNOWN,
    title = title,
    templateId = templateId,
    dateFrom = dateFromEpochDay?.let(LocalDate::ofEpochDay),
    dateTo = dateToEpochDay?.let(LocalDate::ofEpochDay),
    startTime = startTimeMinutes?.let { LocalTime.ofSecondOfDay(it * 60L) },
    endTime = endTimeMinutes?.let { LocalTime.ofSecondOfDay(it * 60L) },
    rrule = rrule,
    alarmMode = alarmMode,
    reminderMinutesBefore = reminderMinutesBefore,
    archivedAtEpochMs = archivedAtEpochMs,
    pausedAtEpochMs = pausedAtEpochMs,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs,
)

fun EntryModel.toEntity(): EntryEntity = EntryEntity(
    id = id,
    kind = kind.storage,
    title = title,
    templateId = templateId,
    dateFromEpochDay = dateFrom?.toEpochDay(),
    dateToEpochDay = dateTo?.toEpochDay(),
    startTimeMinutes = startTime?.toMinuteOfDay(),
    endTimeMinutes = endTime?.toMinuteOfDay(),
    rrule = rrule,
    alarmMode = alarmMode,
    reminderMinutesBefore = reminderMinutesBefore,
    archivedAtEpochMs = archivedAtEpochMs,
    pausedAtEpochMs = pausedAtEpochMs,
    createdAtEpochMs = createdAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs,
)

private fun LocalTime.toMinuteOfDay(): Int = hour * 60 + minute