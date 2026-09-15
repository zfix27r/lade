package app.lade.agenda.api

import app.lade.agenda.api.series.SeriesEditDraft
import app.lade.agenda.api.series.SeriesEditScope
import java.time.LocalDate

interface SeriesEditor {
    suspend fun applySeriesEdit(
        entryId: Long,
        date: LocalDate,
        scope: SeriesEditScope,
        draft: SeriesEditDraft,
    ): Long
}