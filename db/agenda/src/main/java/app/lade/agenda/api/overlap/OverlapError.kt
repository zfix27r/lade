package app.lade.agenda.api.overlap

sealed class OverlapError {
    data object CoveredEntryMissing : OverlapError()
    data object CoveringEntryMissing : OverlapError()
    data object InvalidInterval : OverlapError()
    data object CutNotInside : OverlapError()
    data object CutEqualsCovering : OverlapError()
}