package app.lade.agenda.data.entry

import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryModel
import app.lade.entry.EntryKind
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryValidator @Inject constructor() {
    fun validate(entry: EntryModel): EntryError? {
        if (entry.title.isBlank()) return EntryError.TitleMissing
        return when (entry.entryKind) {
            EntryKind.NOTE -> TODO()
            EntryKind.TASK -> {
                if (entry.dateFrom == null) EntryError.DateMissing else null
            }
            EntryKind.EVENT -> when {
                entry.dateFrom == null -> EntryError.DateMissing
                else -> validateTimeRange(entry.startTime, entry.endTime, required = true)
            }
            EntryKind.HABIT -> {
                if (entry.rrule.isNullOrBlank()) EntryError.RecurrenceMissing else null
            }
            EntryKind.SCHEDULE -> when {
                entry.dateFrom == null -> EntryError.DateMissing
                else -> validateTimeRange(entry.startTime, entry.endTime, required = false)
            }
        }
    }

    private fun validateTimeRange(
        start: LocalTime?,
        end: LocalTime?,
        required: Boolean,
    ): EntryError? = when {
        start == null && end == null -> if (required) EntryError.TimeRangeMissing else null
        start == null || end == null -> EntryError.TimeRangeMissing
        end <= start -> EntryError.TimeRangeInvalid
        else -> null
    }
}