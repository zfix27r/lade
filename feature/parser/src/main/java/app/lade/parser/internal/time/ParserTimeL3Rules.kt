package app.lade.parser.internal.time

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult
import java.time.LocalTime

internal class ParserTimeL3Rules : ParserRule {

    override val priority: Int = ParserPriority.L3

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)
        if (!ParserContract.isFind(entry.timeFrom)) return ParserRuleResult(model, remaining)

        for (rule in RULES) {
            val match = rule.regex.find(remaining) ?: continue
            val time = rule.resolve(match) ?: continue
            return ParserRuleResult(
                model.copy(entry = entry.copy(timeFrom = ParserContract.found(time.toString()))),
                remaining.removeRange(match.range),
            )
        }
        return ParserRuleResult(model, remaining)
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