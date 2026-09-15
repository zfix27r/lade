package app.lade.agenda.data.overlap

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.overlap.OverlapModel
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlapValidator @Inject constructor() {
    fun validate(
        choice: OverlapChoice,
        overlapModel: OverlapModel,
        coveringEntry: EntryModel,
    ): OverlapError? {
        val start = coveringEntry.startTime ?: return OverlapError.CoveringEntryMissing
        val end = coveringEntry.endTime ?: return OverlapError.CoveringEntryMissing
        if (end <= start) return OverlapError.InvalidInterval

        return when (choice) {
            OverlapChoice.DELETE_COVERED -> null
            OverlapChoice.SPLIT_COVERING -> {
                val cutStart = overlapModel.covered.start
                val cutEnd = overlapModel.covered.end
                when {
                    cutEnd <= cutStart -> OverlapError.InvalidInterval
                    cutStart < start || cutEnd > end -> OverlapError.CutNotInside
                    cutStart == start && cutEnd == end -> OverlapError.CutEqualsCovering
                    else -> null
                }
            }
        }
    }
}