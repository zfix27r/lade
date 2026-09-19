package app.lade.agenda.data.entry

import app.lade.agenda.api.entry.EntryModel
import app.lade.agendastore.entry.EntryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryStore @Inject constructor(
    private val entryDao: EntryDao,
) {
    fun observeAll(): Flow<List<EntryModel>> =
        entryDao.observeAll().map { list -> list.map { it.toApi() } }

    fun observeArchived(): Flow<List<EntryModel>> =
        entryDao.observeArchived().map { list -> list.map { it.toApi() } }

    suspend fun getById(id: Long): EntryModel? =
        entryDao.getById(id)?.toApi()

    suspend fun save(entry: EntryModel): Long {
        return if (entry.id == 0L) {
            entryDao.insert(entry.toEntity())
        } else {
            entryDao.update(entry.toEntity())
            entry.id
        }
    }

    suspend fun archive(entryId: Long) {
        val target = entryDao.getById(entryId) ?: return
        entryDao.update(target.copy(archivedAtEpochMs = System.currentTimeMillis()))
    }

    suspend fun restore(entryId: Long) {
        val target = entryDao.getById(entryId) ?: return
        entryDao.update(target.copy(archivedAtEpochMs = null))
    }
}