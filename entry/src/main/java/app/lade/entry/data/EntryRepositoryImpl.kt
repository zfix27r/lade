package app.lade.entry.data

import app.lade.database.entry.EntryDao
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.models.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepositoryImpl @Inject constructor(
	private val dao: EntryDao,
) : EntryRepository {
	override fun observeActive(): Flow<List<Entry>> =
		dao.observeActive().map { list -> list.map { it.toDomain() } }

	override fun observeAll(): Flow<List<Entry>> =
		dao.observeAll().map { list -> list.map { it.toDomain() } }

	override fun observeArchived(): Flow<List<Entry>> =
		dao.observeArchived().map { list -> list.map { it.toDomain() } }

	override suspend fun getById(id: Long): Entry? =
		dao.getById(id)?.toDomain()

	override suspend fun save(entry: Entry): Long {
		return if (entry.id == 0L) {
			dao.insert(entry.toEntity().copy(id = 0))
		} else {
			dao.update(entry.toEntity())
			entry.id
		}
	}

	override suspend fun count(): Int = dao.count()
}

object EntryHistoryResult {
    const val DONE = "done"
    const val SKIPPED = "skipped"
    const val CANCELLED = "cancelled"
}