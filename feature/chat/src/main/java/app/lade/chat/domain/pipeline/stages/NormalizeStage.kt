package app.lade.chat.domain.pipeline.stages

import app.lade.chat.domain.pipeline.ParseContext
import app.lade.chat.domain.pipeline.ParseStage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NormalizeStage @Inject constructor() : ParseStage {
	override suspend fun process(ctx: ParseContext): ParseContext {
		val normalized = ctx.raw.trim().lowercase()
		val tokens = normalized.split(WHITESPACE).filter { it.isNotEmpty() }
		return ctx.copy(normalized = normalized, tokens = tokens)
	}

	companion object {
		private val WHITESPACE = Regex("\\s+")
	}
}
