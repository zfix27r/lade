package app.lade.chat.domain

/**
 * Optional AI / LLM hook: improve raw text → normalized phrase for the same parse pipeline.
 * Default: identity (no-op). Implementations must not invent a different command AST.
 */
fun interface ChatParseEnhancer {
	suspend fun enhance(raw: String): String
}

/** Built-in: pass-through. */
class IdentityChatParseEnhancer @javax.inject.Inject constructor() : ChatParseEnhancer {
	override suspend fun enhance(raw: String): String = raw
}
