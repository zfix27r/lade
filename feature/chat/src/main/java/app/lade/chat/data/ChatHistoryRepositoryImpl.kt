package app.lade.chat.data

import app.lade.chat.domain.ChatHistoryRepository
import app.lade.chat.domain.model.ChatMessage
import app.lade.chatstore.ChatMessageDao
import app.lade.chatstore.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatHistoryRepositoryImpl @Inject constructor(
	private val dao: ChatMessageDao,
) : ChatHistoryRepository {
	override fun observeAll(): Flow<List<ChatMessage>> =
		dao.observeAll().map { list -> list.map { it.toDomain() } }

	override suspend fun append(message: ChatMessage): Long {
		if (message.kind == ChatMessage.KIND_CHOICE) return 0L
		return dao.insert(message.toEntity())
	}

	override suspend fun clear() = dao.clearAll()
}

private fun ChatMessageEntity.toDomain() = ChatMessage(
	id = id,
	kind = kind,
	text = text,
	messageKey = messageKey,
	detail = detail,
	createdAtEpochMs = createdAtEpochMs,
)

private fun ChatMessage.toEntity() = ChatMessageEntity(
	id = if (id > 0) id else 0,
	kind = kind,
	text = text,
	messageKey = messageKey,
	detail = detail,
	createdAtEpochMs = createdAtEpochMs.ifZero { System.currentTimeMillis() },
)

private inline fun Long.ifZero(block: () -> Long): Long = if (this == 0L) block() else this
