package app.lade.chat.domain.pipeline.stages

import app.lade.chat.domain.pipeline.ParseContext
import app.lade.chat.domain.pipeline.ParseStage
import app.lade.chat.domain.pipeline.ParseTokenHelpers
import app.lade.chat.domain.pipeline.SlotFact
import app.lade.chat.domain.pipeline.SlotsResult
import app.lade.chat.domain.pipeline.UserIntent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Parse intent restTokens into facts via greedy NUM↔label pairs (both orders).
 */
@Singleton
class SlotsStage @Inject constructor() : ParseStage {
	override suspend fun process(ctx: ParseContext): ParseContext {
		val rest = ctx.intent?.restTokens.orEmpty()
		if (rest.isEmpty() || ctx.intent?.intent == UserIntent.UNCLEAR) {
			return ctx.copy(slots = SlotsResult())
		}
		return ctx.copy(slots = parse(rest))
	}

	private fun parse(tokens: List<String>): SlotsResult {
		val facts = mutableListOf<SlotFact>()
		val leftover = mutableListOf<String>()
		var i = 0
		while (i < tokens.size) {
			val token = tokens[i]
			if (token in STOP_WORDS) {
				i++
				continue
			}
			val num = token.replace(',', '.').toDoubleOrNull()
			val next = tokens.getOrNull(i + 1)
			when {
				num != null && next != null && isUnit(next) -> {
					facts += SlotFact(
						key = KEY_VALUE,
						amount = num,
						unit = ParseTokenHelpers.normalizeUnit(next),
					)
					i += 2
				}
				num != null && next != null && metricKey(next) != null -> {
					facts += SlotFact(
						key = metricKey(next)!!,
						amount = num,
					)
					i += 2
				}
				isUnit(token) && next != null && next.replace(',', '.').toDoubleOrNull() != null -> {
					facts += SlotFact(
						key = KEY_VALUE,
						amount = next.replace(',', '.').toDouble(),
						unit = ParseTokenHelpers.normalizeUnit(token),
					)
					i += 2
				}
				metricKey(token) != null && next != null &&
					next.replace(',', '.').toDoubleOrNull() != null -> {
					facts += SlotFact(
						key = metricKey(token)!!,
						amount = next.replace(',', '.').toDouble(),
					)
					i += 2
				}
				num != null -> {
					facts += SlotFact(key = KEY_VALUE, amount = num)
					i++
				}
				else -> {
					leftover += token
					i++
				}
			}
		}
		return SlotsResult(facts = facts, leftover = leftover)
	}

	private fun isUnit(token: String): Boolean =
		token.lowercase() in ParseTokenHelpers.BUILTIN_UNITS

	private fun metricKey(token: String): String? =
		METRIC_ALIASES[token.lowercase()]

	companion object {
		const val KEY_VALUE = "value"

		private val STOP_WORDS = setOf("с", "и", "а", "на", "по", "the", "a", "with")

		/** Temporary seed; later from dict. */
		private val METRIC_ALIASES = mapOf(
			"пульс" to "пульс",
			"пульсом" to "пульс",
			"пульса" to "пульс",
			"pulse" to "пульс",
			"темп" to "темп",
			"pace" to "темп",
			"каденс" to "каденс",
			"cadence" to "каденс",
		)
	}
}
