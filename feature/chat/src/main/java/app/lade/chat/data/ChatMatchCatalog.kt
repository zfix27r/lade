package app.lade.chat.data

import app.lade.chat.domain.ChatCorpusRepository
import app.lade.chat.domain.ChatDictRepository
import app.lade.chat.domain.ChatMatchCatalog
import app.lade.chat.domain.pipeline.MatchRule
import app.lade.chat.domain.pipeline.MatchRules
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatMatchCatalogImpl @Inject constructor(
	private val corpusRepository: ChatCorpusRepository,
	private val dictRepository: ChatDictRepository,
) : ChatMatchCatalog {
	override suspend fun activeRules(): List<MatchRule> {
		val system = corpusRepository.systemEntries().map { MatchRules.fromCorpus(it) }
		val user = dictRepository.observeActive().first()
			.filter { !it.isSystem }
			.map { MatchRules.fromUserDict(it) }
		return system + user
	}
}
