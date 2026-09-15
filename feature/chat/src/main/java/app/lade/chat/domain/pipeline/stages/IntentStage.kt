package app.lade.chat.domain.pipeline.stages

import app.lade.chat.domain.ChatMatchCatalog
import app.lade.chat.domain.pipeline.IntentResult
import app.lade.chat.domain.pipeline.MatchRules
import app.lade.chat.domain.pipeline.ParseContext
import app.lade.chat.domain.pipeline.ParseStage
import app.lade.chat.domain.pipeline.PhraseMatcher
import app.lade.chat.domain.pipeline.UserIntent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntentStage @Inject constructor(
	private val matchCatalog: ChatMatchCatalog,
) : ParseStage {
	override suspend fun process(ctx: ParseContext): ParseContext {
		if (ctx.tokens.isEmpty()) {
			return ctx.copy(intent = IntentResult(UserIntent.UNCLEAR))
		}
		val hit = PhraseMatcher.findBest(ctx.tokens, matchCatalog.activeRules())
			?: return ctx.copy(intent = IntentResult(UserIntent.UNCLEAR))
		val rest = ctx.tokens.filterIndexed { i, _ ->
			i < hit.start || i >= hit.start + hit.length
		}
		val userIntent = hit.needle.intent
			?: MatchRules.defaultIntentForKind(hit.rule.kind)
		return ctx.copy(
			intent = IntentResult(
				intent = userIntent,
				matchedPhrase = hit.needle.text,
				restTokens = rest,
				kindHint = hit.rule.kind,
				titleHint = hit.rule.title,
				systemKey = hit.rule.systemKey,
			),
		)
	}
}
