package app.lade.agenda.api.entry

sealed class EntryError {
    data object TitleMissing : EntryError()
    data object DateMissing : EntryError()
    data object TimeRangeMissing : EntryError()
    data object TimeRangeInvalid : EntryError()
    data object RecurrenceMissing : EntryError()
}