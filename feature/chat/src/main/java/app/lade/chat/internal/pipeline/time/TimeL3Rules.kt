package app.lade.chat.internal.pipeline.time

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.pipeline.ParseRule
import app.lade.chat.internal.pipeline.Priority
import app.lade.chat.internal.pipeline.RuleResult
import java.time.LocalTime

internal class TimeL3Rules : ParseRule {

    override val priority: Int = Priority.L3

    override fun apply(model: ParserModel, remaining: String): RuleResult {
        val entry = model.entry ?: return RuleResult(model, remaining)
        if (!ParserContract.isFind(entry.timeFrom)) return RuleResult(model, remaining)

        for (rule in RULES) {
            val match = rule.regex.find(remaining) ?: continue
            val time = rule.resolve(match) ?: continue
            return RuleResult(
                model.copy(entry = entry.copy(timeFrom = ParserContract.found(time.toString()))),
                remaining.removeRange(match.range),
            )
        }
        return RuleResult(model, remaining)
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