package app.lade.agenda.data.series

import app.lade.agenda.api.series.SeriesEditDraft
import app.lade.agenda.api.series.SeriesEditScope
import app.lade.agenda.data.entry.toApi
import app.lade.agenda.data.entry.toEntity
import app.lade.agendastore.entry.EntryDao
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesEditor @Inject constructor(
    private val entryDao: EntryDao,
) {
    suspend fun apply(
        entryId: Long,
        date: LocalDate,
        scope: SeriesEditScope,
        draft: SeriesEditDraft,
    ): Long {
        val series = entryDao.getById(entryId)?.toApi()
            ?: error("Entry $entryId not found")
        require(series.isSeries) { "entry is not a series" }
        return when (scope) {
            SeriesEditScope.WHOLE_SERIES -> {
                val updated = series.copy(
                    title = draft.title ?: series.title,
                    startTime = draft.startTime ?: series.startTime,
                    endTime = draft.endTime ?: series.endTime,
                    templateId = draft.templateId ?: series.templateId,
                )
                entryDao.update(updated.toEntity())
                updated.id
            }
            SeriesEditScope.THIS_DAY_ONLY -> {
                val single = series.copy(
                    id = 0,
                    title = draft.title ?: series.title,
                    dateFrom = date,
                    dateTo = date,
                    startTime = draft.startTime ?: series.startTime,
                    endTime = draft.endTime ?: series.endTime,
                    templateId = draft.templateId ?: series.templateId,
                    rrule = null,
                    createdAtEpochMs = System.currentTimeMillis(),
                )
                entryDao.insert(single.toEntity())
            }
        }
    }
}