package app.lade.entrydetailsscreen.data

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.agenda.AgendaError
import app.lade.agenda.api.agenda.AgendaSaveModel
import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapError
import app.lade.agenda.api.overlap.OverlapModel
import app.lade.agenda.api.overlap.OverlapResult
import app.lade.entrydetailsscreen.domain.EntryEditRepository
import app.lade.entrydetailsscreen.domain.model.EntryEditData
import app.lade.notifications.Rescheduler
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryEditRepositoryImpl @Inject constructor(
    private val agendaApi: AgendaApi,
    private val rescheduler: Rescheduler,
) : EntryEditRepository {

    override suspend fun load(entryId: Long, date: LocalDate): EntryEditData? {
        val agenda = agendaApi.get(entryId, date) ?: return null
        return EntryEditData(entry = agenda.entry, goals = agenda.goals)
    }

    override suspend fun save(model: AgendaSaveModel): Result<Long, AgendaError> {
        val result = agendaApi.saveAgenda(model)
        if (result is Result.Success) rescheduler.rescheduleAll()
        return result
    }

    override suspend fun resolveOverlap(
        choice: OverlapChoice,
        conflict: OverlapModel,
        pending: EntryModel,
    ): Result<OverlapResult, OverlapError> {
        val result = agendaApi.resolveOverlap(choice, conflict, pending)
        if (result is Result.Success) rescheduler.rescheduleAll()
        return result
    }

    override suspend fun restore(entryId: Long): Result<Unit, EntryError> {
        val result = agendaApi.restoreEntry(entryId)
        if (result is Result.Success) rescheduler.rescheduleAll()
        return result
    }

    override suspend fun archive(entryId: Long): Result<Unit, EntryError> =
        agendaApi.archiveEntry(entryId)
}