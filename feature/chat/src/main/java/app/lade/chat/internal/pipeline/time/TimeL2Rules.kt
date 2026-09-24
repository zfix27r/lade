package app.lade.chat.internal.pipeline.time

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import java.time.LocalTime

internal class TimeL2Rules : ParseRule {

    override val priority: Int = Priority.L2

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        for (rule in RULES) {
            val match = rule.regex.find(raw) ?: continue
            val resolved = rule.resolve(match) ?: continue
            val filtered = resolved.filterNot { state.contains(it.first) }
            if (filtered.isEmpty()) continue
            return filtered.map { (key, time) ->
                RuleMatch(
                    key = key,
                    value = FieldValue.Time(time),
                    match = match.value,
                    span = match.range,
                )
            }
        }
        return emptyList()
    }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult) -> List<Pair<FieldKey, LocalTime>>?,
    )

    companion object {
        private val RULES: List<Rule> = listOf(
            Rule(
                regex = Regex("""с\s+(\d{1,2}):(\d{2})\s+до\s+(\d{1,2}):(\d{2})"""),
            ) { match ->
                val from = safeTime(match.groupValues[1].toInt(), match.groupValues[2].toInt()) ?: return@Rule null
                val to = safeTime(match.groupValues[3].toInt(), match.groupValues[4].toInt()) ?: return@Rule null
                listOf(FieldKey.TIME_FROM to from, FieldKey.TIME_END to to)
            },

            Rule(
                regex = Regex("""(\d{1,2}):(\d{2})\s*[–-]\s*(\d{1,2}):(\d{2})"""),
            ) { match ->
                val from = safeTime(match.groupValues[1].toInt(), match.groupValues[2].toInt()) ?: return@Rule null
                val to = safeTime(match.groupValues[3].toInt(), match.groupValues[4].toInt()) ?: return@Rule null
                listOf(FieldKey.TIME_FROM to from, FieldKey.TIME_END to to)
            },

            Rule(
                regex = Regex("""в\s+(\d{1,2}):(\d{2})"""),
            ) { match ->
                val time = safeTime(match.groupValues[1].toInt(), match.groupValues[2].toInt()) ?: return@Rule null
                listOf(FieldKey.TIME_FROM to time)
            },

            Rule(
                regex = Regex("""(\d{1,2}):(\d{2})"""),
            ) { match ->
                val time = safeTime(match.groupValues[1].toInt(), match.groupValues[2].toInt()) ?: return@Rule null
                listOf(FieldKey.TIME_FROM to time)
            },
        )

        private fun safeTime(hour: Int, minute: Int): LocalTime? =
            try {
                LocalTime.of(hour, minute)
            } catch (_: Exception) {
                null
            }
    }
}