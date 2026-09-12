package app.lade.entry.domain

import app.lade.entry.domain.models.Entry
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
	fun observeActive(): Flow<List<Entry>>
	fun observeAll(): Flow<List<Entry>>
	fun observeArchived(): Flow<List<Entry>>
	suspend fun getById(id: Long): Entry?
	suspend fun save(entry: Entry): Long
	suspend fun count(): Int
}
