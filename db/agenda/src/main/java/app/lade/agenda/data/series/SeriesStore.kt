package app.lade.agenda.data.series

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.series.SeriesEditDraft
import app.lade.agenda.api.series.SeriesEditScope
import app.lade.agenda.data.entry.toEntity
import app.lade.agendastore.entry.EntryDao
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesStore @Inject constructor(
    private val entryDao: EntryDao,
) {
    suspend fun apply(
        entry: EntryModel,
        date: LocalDate,
        scope: SeriesEditScope,
        draft: SeriesEditDraft,
    ): Long {
        return when (scope) {
            SeriesEditScope.WHOLE_SERIES -> {
                val updated = entry.copy(
                    title = draft.title ?: entry.title,
                    startTime = draft.startTime ?: entry.startTime,
                    endTime = draft.endTime ?: entry.endTime,
                    templateId = draft.templateId ?: entry.templateId,
                )
                entryDao.update(updated.toEntity())
                updated.id
            }
            SeriesEditScope.THIS_DAY_ONLY -> {
                val single = entry.copy(
                    id = 0,
                    title = draft.title ?: entry.title,
                    dateFrom = date,
                    dateTo = date,
                    startTime = draft.startTime ?: entry.startTime,
                    endTime = draft.endTime ?: entry.endTime,
                    templateId = draft.templateId ?: entry.templateId,
                    rrule = null,
                    createdAtEpochMs = System.currentTimeMillis(),
                )
                entryDao.insert(single.toEntity())
            }
        }
    }
}