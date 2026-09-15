package app.lade.agenda.api.series

sealed class SeriesError {
    data object EntryMissing : SeriesError()
    data object NotASeries : SeriesError()
    data object InvalidTimeRange : SeriesError()
    data object EmptyDraft : SeriesError()
}