package app.lade.chat.domain.pipeline

import app.lade.chat.domain.ChatDictRepository
import app.lade.chat.domain.pipeline.stages.DecideStage
import app.lade.chat.domain.pipeline.stages.IntentStage
import app.lade.chat.domain.pipeline.stages.NormalizeStage
import app.lade.chat.domain.pipeline.stages.ResolveStage
import app.lade.chat.domain.pipeline.stages.SlotsStage
import app.lade.chat.domain.pipeline.stages.TailStage
import app.lade.entry.domain.EntryRepository
import app.lade.reminders.DayPartPreferences
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Target pipeline: normalize → intent → slots → tail → resolve → decide → [ParseOutcome].
 */
@Singleton
class ChatParseOrchestrator @Inject constructor(
	private val normalizeStage: NormalizeStage,
	private val intentStage: IntentStage,
	private val slotsStage: SlotsStage,
	private val tailStage: TailStage,
	private val resolveStage: ResolveStage,
	private val decideStage: DecideStage,
	private val entryRepository: EntryRepository,
	private val dictRepository: ChatDictRepository,
	private val dayPartPreferences: DayPartPreferences,
) {
	suspend fun run(
		raw: String,
		today: LocalDate = LocalDate.now(),
	): ParseOutcome {
		var ctx = ParseContext(
			raw = raw,
			today = today,
			entries = entryRepository.observeActive().first(),
			dicts = dictRepository.observeActive().first().filter { !it.isSystem },
			dayPartClock = dayPartPreferences.clock(),
		)
		ctx = normalizeStage.process(ctx)
		if (ctx.tokens.isEmpty()) return ParseOutcome.Failed("empty")
		ctx = intentStage.process(ctx)
		ctx = slotsStage.process(ctx)
		ctx = tailStage.process(ctx)
		val draft = ParseDraft(
			intent = ctx.intent ?: IntentResult(UserIntent.UNCLEAR),
			slots = ctx.slots ?: SlotsResult(),
			tail = ctx.tail ?: TailResult(date = ctx.today, title = ""),
		)
		if (draft.intent.intent == UserIntent.UNCLEAR) {
			return ParseOutcome.Failed("parse")
		}
		val resolved = resolveStage.resolve(draft, ctx.entries, ctx.dicts)
		return decideStage.decide(resolved)
	}

	/** Debug / tests: draft without resolve/decide. */
	suspend fun runDraft(
		raw: String,
		today: LocalDate = LocalDate.now(),
	): ParseDraft {
		var ctx = ParseContext(raw = raw, today = today, dayPartClock = dayPartPreferences.clock())
		ctx = normalizeStage.process(ctx)
		ctx = intentStage.process(ctx)
		ctx = slotsStage.process(ctx)
		ctx = tailStage.process(ctx)
		return ParseDraft(
			intent = ctx.intent ?: IntentResult(UserIntent.UNCLEAR),
			slots = ctx.slots ?: SlotsResult(),
			tail = ctx.tail ?: TailResult(date = ctx.today, title = ""),
		)
	}
}
