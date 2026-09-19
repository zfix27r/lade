package app.lade.entrydetailsscreen.data

import app.lade.agenda.api.Result
import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryModel
import kotlinx.coroutines.flow.Flow

interface EntryListRepository {
    fun observeAll(): Flow<List<EntryModel>>
    fun observeArchived(): Flow<List<EntryModel>>
    suspend fun archive(entryId: Long): Result<Unit, EntryError>
    suspend fun restore(entryId: Long): Result<Unit, EntryError>
}