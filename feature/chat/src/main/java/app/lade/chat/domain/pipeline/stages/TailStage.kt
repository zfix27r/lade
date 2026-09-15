package app.lade.chat.domain.pipeline.stages

import app.lade.chat.domain.pipeline.ParseContext
import app.lade.chat.domain.pipeline.ParseStage
import app.lade.chat.domain.pipeline.TailResult
import app.lade.chat.domain.pipeline.TailTokenParser
import app.lade.chat.domain.pipeline.UserIntent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Date/time/title/actuals from slot leftover after [SlotsStage].
 */
@Singleton
class TailStage @Inject constructor() : ParseStage {
	override suspend fun process(ctx: ParseContext): ParseContext {
		val intent = ctx.intent
		if (intent == null || intent.intent == UserIntent.UNCLEAR) {
			return ctx.copy(tail = emptyTail(ctx))
		}
		val slots = ctx.slots
		val tokens = slots?.leftover?.takeIf { it.isNotEmpty() }
			?: intent.restTokens
		val tail = TailTokenParser.parse(
			TailTokenParser.Input(
				tokens = tokens,
				today = ctx.today,
				dayPartClock = ctx.dayPartClock,
				titleHint = intent.titleHint,
				userIntent = intent.intent,
				slotFacts = slots?.facts.orEmpty(),
			),
		)
		return ctx.copy(tail = tail)
	}

	private fun emptyTail(ctx: ParseContext): TailResult = TailResult(
		date = ctx.today,
		title = "",
	)
}
