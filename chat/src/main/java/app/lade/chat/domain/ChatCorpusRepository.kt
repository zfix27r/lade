package app.lade.chat.domain

import app.lade.chat.domain.model.CorpusEntry
import kotlinx.coroutines.flow.Flow

interface ChatCorpusRepository {
	fun observeSystem(): Flow<List<CorpusEntry>>
	suspend fun systemEntries(): List<CorpusEntry>
	suspend fun reload()
}
