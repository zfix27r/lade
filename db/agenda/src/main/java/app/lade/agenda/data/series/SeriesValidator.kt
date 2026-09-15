package app.lade.agenda.data.series

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.series.SeriesEditDraft
import app.lade.agenda.api.series.SeriesEditScope
import app.lade.agenda.api.series.SeriesError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesValidator @Inject constructor() {
    fun validate(
        entry: EntryModel,
        scope: SeriesEditScope,
        draft: SeriesEditDraft,
    ): SeriesError? {
        if (!entry.isSeries) return SeriesError.NotASeries
        if (draft.isEmpty()) return SeriesError.EmptyDraft
        draft.startTime?.let { start ->
            draft.endTime?.let { end ->
                if (end <= start) return SeriesError.InvalidTimeRange
            }
        }
        return null
    }

    private fun SeriesEditDraft.isEmpty(): Boolean =
        title == null && startTime == null && endTime == null && templateId == null
}