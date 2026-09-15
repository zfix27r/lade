package app.lade.chat.domain.pipeline

/**
 * Match tokens against exact phrases or stems (`*беж*`).
 */
object PhraseMatcher {
	private const val MIN_STEM_LEN = 3

	data class Hit(
		val rule: MatchRule,
		val needle: Needle,
		val start: Int,
		val length: Int,
	)

	fun findBest(tokens: List<String>, rules: List<MatchRule>): Hit? {
		var best: Hit? = null
		for (rule in rules) {
			for (needle in rule.needles) {
				val hit = matchNeedle(tokens, rule, needle) ?: continue
				if (best == null || hit.beats(best)) {
					best = hit
				}
			}
		}
		return best
	}

	private fun matchNeedle(
		tokens: List<String>,
		rule: MatchRule,
		needle: Needle,
	): Hit? {
		val trimmed = needle.text.trim().lowercase()
		if (trimmed.isEmpty()) return null
		if (isStem(trimmed)) {
			val stem = stemBody(trimmed)
			if (stem.length < MIN_STEM_LEN) return null
			for (i in tokens.indices) {
				if (tokens[i].contains(stem)) {
					return Hit(rule, needle, i, 1)
				}
			}
			return null
		}
		val parts = trimmed.split(Regex("\\s+")).filter { it.isNotEmpty() }
		if (parts.isEmpty() || parts.size > tokens.size) return null
		for (start in 0..tokens.size - parts.size) {
			if (tokens.subList(start, start + parts.size) == parts) {
				return Hit(rule, needle, start, parts.size)
			}
		}
		return null
	}

	private fun isStem(needle: String): Boolean =
		needle.startsWith("*") && needle.endsWith("*") && needle.length > 2

	private fun stemBody(needle: String): String =
		needle.removePrefix("*").removeSuffix("*")

	private fun Hit.beats(other: Hit): Boolean {
		if (length != other.length) return length > other.length
		val intentScore = intentPriority(needle.intent ?: MatchRules.defaultIntentForKind(rule.kind))
		val otherIntentScore = intentPriority(
			other.needle.intent ?: MatchRules.defaultIntentForKind(other.rule.kind),
		)
		if (intentScore != otherIntentScore) return intentScore > otherIntentScore
		return needle.text.length > other.needle.text.length
	}

	/** MARK_DONE wins over NAME_OR_CREATE when phrase length ties. */
	private fun intentPriority(intent: UserIntent): Int = when (intent) {
		UserIntent.MARK_DONE -> 2
		UserIntent.NAME_OR_CREATE -> 1
		UserIntent.TIMED -> 1
		UserIntent.CREATE_KIND -> 1
		UserIntent.UNCLEAR -> 0
	}
}
