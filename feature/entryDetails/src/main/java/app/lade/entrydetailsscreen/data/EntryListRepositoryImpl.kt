package app.lade.entrydetailsscreen.data

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryListRepositoryImpl @Inject constructor(
    private val agendaApi: AgendaApi,
) : EntryListRepository {

    override fun observeAll(): Flow<List<EntryModel>> = agendaApi.observeAllEntries()

    override fun observeArchived(): Flow<List<EntryModel>> = agendaApi.observeArchivedEntries()

    override suspend fun archive(entryId: Long): Result<Unit, EntryError> =
        agendaApi.archiveEntry(entryId)

    override suspend fun restore(entryId: Long): Result<Unit, EntryError> =
        agendaApi.restoreEntry(entryId)
}