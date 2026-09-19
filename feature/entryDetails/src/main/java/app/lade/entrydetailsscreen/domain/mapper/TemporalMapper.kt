package app.lade.entrydetailsscreen.domain.mapper

import app.lade.agenda.api.entry.EntryModel
import app.lade.entrydetailsscreen.domain.model.EntryEditUiState
import app.lade.schedule.data.TemporalOptions
import app.lade.schedule.ui.AlarmModeOption
import app.lade.temporal.api.RecurrenceDraft

object TemporalMapper {

    fun toTemporal(entry: EntryModel): TemporalOptions = TemporalOptions(
        dateFrom = entry.dateFrom,
        dateTo = entry.dateTo,
        time = entry.startTime,
        timeEnd = entry.endTime,
        recurrence = RecurrenceDraft.fromRrule(entry.rrule),
        alarmMode = AlarmModeOption.fromStorage(entry.alarmMode),
        reminderMinutesBefore = entry.reminderMinutesBefore,
    )

    fun toEntryModel(state: EntryEditUiState): EntryModel {
        val t = state.temporal
        return EntryModel(
            id = state.id,
            kind = state.effectiveKind,
            title = state.title.trim(),
            templateId = state.templateId,
            dateFrom = t.dateFrom,
            dateTo = t.dateTo,
            startTime = t.time,
            endTime = t.timeEnd,
            rrule = t.recurrence.toRrule().ifBlank { null },
            alarmMode = t.alarmMode.storage,
            reminderMinutesBefore = t.reminderMinutesBefore,
            createdAtEpochMs = state.createdAtEpochMs,
        )
    }
}