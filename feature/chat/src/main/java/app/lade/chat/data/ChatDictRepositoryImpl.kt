package app.lade.chat.data

import app.lade.chat.domain.ChatDictRepository
import app.lade.chat.domain.model.ChatDictEntry
import app.lade.chatstore.ChatDictDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatDictRepositoryImpl @Inject constructor(
	private val dao: ChatDictDao,
) : ChatDictRepository {
	override fun observeActive(): Flow<List<ChatDictEntry>> =
		dao.observeActive().map { list ->
			list.map { it.toDomain() }.filter { !it.isSystem }
		}

	override suspend fun getById(id: Long): ChatDictEntry? =
		dao.getById(id)?.toDomain()?.takeUnless { it.isSystem }

	override suspend fun save(entry: ChatDictEntry): Long {
		val user = entry.copy(systemKey = null)
		return if (user.id == 0L) {
			dao.insert(user.toEntity().copy(id = 0, systemKey = null))
		} else {
			dao.update(user.toEntity().copy(systemKey = null))
			user.id
		}
	}

	override suspend fun archive(id: Long) {
		dao.archive(id, System.currentTimeMillis())
	}
}
