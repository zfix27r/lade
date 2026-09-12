package app.lade.chat.domain

import app.lade.chat.domain.pipeline.MatchRule

interface ChatMatchCatalog {
	suspend fun activeRules(): List<MatchRule>
}
