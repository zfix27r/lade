package app.lade.agenda.data.entry

import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryValidator @Inject constructor() {
    fun validate(entry: EntryModel): EntryError? {
        if (entry.title.isBlank()) return EntryError.TitleMissing
        return when (entry.kind) {
            EntryKind.TASK -> {
                if (entry.dateFrom == null) EntryError.DateMissing else null
            }
            EntryKind.EVENT -> when {
                entry.dateFrom == null -> EntryError.DateMissing
                entry.startTime == null || entry.endTime == null -> EntryError.TimeRangeMissing
                entry.endTime <= entry.startTime -> EntryError.TimeRangeInvalid
                else -> null
            }
            EntryKind.HABIT -> {
                if (entry.rrule.isNullOrBlank()) EntryError.RecurrenceMissing else null
            }
            EntryKind.SCHEDULE -> when {
                entry.startTime == null || entry.endTime == null -> EntryError.TimeRangeMissing
                entry.endTime <= entry.startTime -> EntryError.TimeRangeInvalid
                entry.rrule.isNullOrBlank() -> EntryError.RecurrenceMissing
                else -> null
            }
            EntryKind.UNKNOWN -> null
        }
    }
}