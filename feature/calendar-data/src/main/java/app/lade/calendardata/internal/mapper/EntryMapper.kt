package app.lade.calendardata.internal.mapper

import app.lade.agendastore.entry.EntryEntity
import app.lade.entry.EntryKind
import java.time.LocalTime

internal fun EntryEntity.toEntryKind(): EntryKind? =
    EntryKind.fromStorage(kind)

internal fun EntryEntity.toLocalTimeFrom(): LocalTime? =
    startTimeMinutes?.let { LocalTime.of(it / 60, it % 60) }

internal fun EntryEntity.toLocalTimeTo(): LocalTime? =
    endTimeMinutes?.let { LocalTime.of(it / 60, it % 60) }

internal fun EntryEntity.isSeries(): Boolean = !rrule.isNullOrBlank()

internal fun EntryEntity.isAlarm(): Boolean = alarmMode != "none"