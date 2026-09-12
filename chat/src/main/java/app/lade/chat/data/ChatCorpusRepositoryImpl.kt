package app.lade.chat.data

import app.lade.chat.domain.ChatCorpusRepository
import app.lade.chat.domain.model.CorpusEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatCorpusRepositoryImpl @Inject constructor(
	private val loader: ChatCorpusLoader,
) : ChatCorpusRepository {
	private val mutex = Mutex()
	private val _system = MutableStateFlow(loadLocked())
	val systemFlow = _system.asStateFlow()

	override fun observeSystem(): Flow<List<CorpusEntry>> = systemFlow

	override suspend fun systemEntries(): List<CorpusEntry> = mutex.withLock {
		_system.value
	}

	override suspend fun reload() {
		mutex.withLock {
			_system.value = loader.loadAll()
		}
	}

	private fun loadLocked(): List<CorpusEntry> = loader.loadAll()
}
