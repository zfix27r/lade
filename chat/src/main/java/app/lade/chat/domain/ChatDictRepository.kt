package app.lade.chat.domain

import app.lade.chat.domain.model.ChatDictEntry
import kotlinx.coroutines.flow.Flow

interface ChatDictRepository {
	fun observeActive(): Flow<List<ChatDictEntry>>
	suspend fun getById(id: Long): ChatDictEntry?
	suspend fun save(entry: ChatDictEntry): Long
	suspend fun archive(id: Long)
}
