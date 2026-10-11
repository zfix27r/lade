package app.lade.parser.internal.time

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult
import java.time.LocalTime

internal class ParserTimeL2Rules : ParserRule {

    override val priority: Int = ParserPriority.L2

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)

        for (rule in RULES) {
            val match = rule.regex.find(remaining) ?: continue
            val resolved = rule.resolve(match) ?: continue
            val applicable = resolved.filter { (field) ->
                when (field) {
                    Field.TIME_FROM -> ParserContract.isFind(entry.timeFrom)
                    Field.TIME_END -> ParserContract.isFind(entry.timeTo)
                }
            }
            if (applicable.isEmpty()) continue

            var updated = entry
            applicable.forEach { (field, time) ->
                updated = when (field) {
                    Field.TIME_FROM -> updated.copy(timeFrom = ParserContract.found(time.toString()))
                    Field.TIME_END -> updated.copy(timeTo = ParserContract.found(time.toString()))
                }
            }

            return ParserRuleResult(
                model.copy(entry = updated),
                remaining.removeRange(match.range),
            )
        }
        return ParserRuleResult(model, remaining)
    }

    private enum class Field { TIME_FROM, TIME_END }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult) -> List<Pair<Field, LocalTime>>?,
    )

    companion object {
        private val RULES: List<Rule> = listOf(
            Rule(
                regex = Regex("""с\s+(\d{1,2}):(\d{2})\s+до\s+(\d{1,2}):(\d{2})"""),
            ) { match ->
                val from = safeTime(match.groupValues[1].toInt(), match.groupValues[2].toInt()) ?: return@Rule null
                val to = safeTime(match.groupValues[3].toInt(), match.groupValues[4].toInt()) ?: return@Rule null
                listOf(Field.TIME_FROM to from, Field.TIME_END to to)
            },

            Rule(
                regex = Regex("""(\d{1,2}):(\d{2})\s*[–-]\s*(\d{1,2}):(\d{2})"""),
            ) { match ->
                val from = safeTime(match.groupValues[1].toInt(), match.groupValues[2].toInt()) ?: return@Rule null
                val to = safeTime(match.groupValues[3].toInt(), match.groupValues[4].toInt()) ?: return@Rule null
                listOf(Field.TIME_FROM to from, Field.TIME_END to to)
            },

            Rule(
                regex = Regex("""в\s+(\d{1,2}):(\d{2})"""),
            ) { match ->
                val time = safeTime(match.groupValues[1].toInt(), match.groupValues[2].toInt()) ?: return@Rule null
                listOf(Field.TIME_FROM to time)
            },

            Rule(
                regex = Regex("""(\d{1,2}):(\d{2})"""),
            ) { match ->
                val time = safeTime(match.groupValues[1].toInt(), match.groupValues[2].toInt()) ?: return@Rule null
                listOf(Field.TIME_FROM to time)
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