package app.lade.agenda.data

import app.lade.agenda.api.EntryValidator
import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryMissingField
import app.lade.agenda.api.entry.EntryModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryValidatorImpl @Inject constructor() : EntryValidator {
    override suspend fun validate(entry: EntryModel): EntryMissingField? {
        if (entry.title.isBlank()) return EntryMissingField.TITLE
        return when (entry.kind) {
            EntryKind.TASK -> if (entry.dateFrom == null) EntryMissingField.DATE else null
            EntryKind.EVENT -> when {
                entry.dateFrom == null -> EntryMissingField.DATE
                entry.startTime == null || entry.endTime == null -> EntryMissingField.TIME_RANGE
                entry.endTime <= entry.startTime -> EntryMissingField.TIME_RANGE
                else -> null
            }
            EntryKind.HABIT -> if (entry.rrule.isNullOrBlank()) EntryMissingField.RECURRENCE else null
            EntryKind.SCHEDULE -> when {
                entry.startTime == null || entry.endTime == null -> EntryMissingField.TIME_RANGE
                entry.endTime <= entry.startTime -> EntryMissingField.TIME_RANGE
                entry.rrule.isNullOrBlank() -> EntryMissingField.RECURRENCE
                else -> null
            }
            EntryKind.UNKNOWN -> null
        }
    }
}