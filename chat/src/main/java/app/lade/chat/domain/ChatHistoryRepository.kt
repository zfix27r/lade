package app.lade.chat.domain

import app.lade.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatHistoryRepository {
	fun observeAll(): Flow<List<ChatMessage>>
	suspend fun append(message: ChatMessage): Long
	suspend fun clear()
}
