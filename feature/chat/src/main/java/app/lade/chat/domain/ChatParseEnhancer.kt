package app.lade.chat.domain

fun interface ChatParseEnhancer {
	suspend fun enhance(raw: String): String
}

class IdentityChatParseEnhancer @javax.inject.Inject constructor() : ChatParseEnhancer {
	override suspend fun enhance(raw: String): String = raw
}
