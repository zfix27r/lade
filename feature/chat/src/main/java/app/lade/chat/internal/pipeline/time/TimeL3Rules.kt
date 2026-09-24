package app.lade.chat.internal.pipeline.time

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import java.time.LocalTime

internal class TimeL3Rules : ParseRule {

    override val priority: Int = Priority.L3

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        for (rule in RULES) {
            val match = rule.regex.find(raw) ?: continue
            val time = rule.resolve(match) ?: continue
            if (state.contains(FieldKey.TIME_FROM)) continue
            return listOf(
                RuleMatch(
                    key = FieldKey.TIME_FROM,
                    value = FieldValue.Time(time),
                    match = match.value,
                    span = match.range,
                ),
            )
        }
        return emptyList()
    }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult) -> LocalTime?,
    )

    companion object {
        private val RULES: List<Rule> = listOf(
            Rule(
                regex = Regex("""в\s+(\d{1,2})\s+утра"""),
            ) { match ->
                safeTime(match.groupValues[1].toInt(), 0)
            },

            Rule(
                regex = Regex("""в\s+(\d{1,2})\s+дня"""),
            ) { match ->
                val hour = match.groupValues[1].toInt()
                if (hour in 1..11) safeTime(hour + 12, 0) else null
            },

            Rule(
                regex = Regex("""в\s+(\d{1,2})\s+вечера"""),
            ) { match ->
                val hour = match.groupValues[1].toInt()
                if (hour in 1..11) safeTime(hour + 12, 0) else null
            },

            Rule(
                regex = Regex("""в\s+(\d{1,2})\s+ночи"""),
            ) { match ->
                val hour = match.groupValues[1].toInt()
                if (hour in 0..5) safeTime(hour, 0) else null
            },

            Rule(
                regex = Regex("""(\d{1,2})\s+утра"""),
            ) { match ->
                safeTime(match.groupValues[1].toInt(), 0)
            },

            Rule(
                regex = Regex("""(?<!раз\s)в\s+(\d{1,2})(?:\s+час\w*)?(?!\s+(?:дн|недел|месяц|год|лет))"""),
            ) { match ->
                val hour = match.groupValues[1].toInt()
                if (hour in 0..23) safeTime(hour, 0) else null
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