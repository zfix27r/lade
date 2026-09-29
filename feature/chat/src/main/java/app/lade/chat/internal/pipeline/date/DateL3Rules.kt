package app.lade.chat.internal.pipeline.date

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.pipeline.ParseRule
import app.lade.chat.internal.pipeline.Priority
import app.lade.chat.internal.pipeline.RuleResult
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal class DateL3Rules(
    private val today: LocalDate,
) : ParseRule {

    override val priority: Int = Priority.L3

    override fun apply(model: ParserModel, remaining: String): RuleResult {
        val entry = model.entry ?: return RuleResult(model, remaining)
        if (!ParserContract.isFind(entry.dateFrom)) return RuleResult(model, remaining)

        for (rule in RULES) {
            val match = rule.regex.find(remaining) ?: continue
            val date = rule.resolve(match, today) ?: continue
            return RuleResult(
                model.copy(entry = entry.copy(dateFrom = ParserContract.found(date.toString()))),
                remaining.removeRange(match.range),
            )
        }

        val words = Regex("""[а-яё]+""", RegexOption.IGNORE_CASE).findAll(remaining)
        for (word in words) {
            val day = day(word.value) ?: continue
            val date = today.with(TemporalAdjusters.nextOrSame(day))
            return RuleResult(
                model.copy(entry = entry.copy(dateFrom = ParserContract.found(date.toString()))),
                remaining.removeRange(word.range),
            )
        }

        return RuleResult(model, remaining)
    }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult, LocalDate) -> LocalDate?,
    )

    companion object {
        private val DAYS: List<Pair<Regex, DayOfWeek>> = listOf(
            Regex("""пон?едельник\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.MONDAY,
            Regex("""вторник\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.TUESDAY,
            Regex("""сред[аыу]\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.WEDNESDAY,
            Regex("""четверг\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.THURSDAY,
            Regex("""пятниц[аыу]\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.FRIDAY,
            Regex("""суббот[аыу]\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.SATURDAY,
            Regex("""воскресень[ея]\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.SUNDAY,
        )

        private fun day(text: String): DayOfWeek? =
            DAYS.firstOrNull { it.first.matches(text) }?.second

        private fun safeDate(year: Int, month: Int, day: Int): LocalDate? =
            try {
                LocalDate.of(year, month, day)
            } catch (_: Exception) {
                null
            }

        private val RULES: List<Rule> = listOf(
            Rule(
                regex = Regex("""следующ\p{L}*\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val day = day(match.groupValues[1]) ?: return@Rule null
                today.with(TemporalAdjusters.next(day))
            },

            Rule(
                regex = Regex("""(\d{1,2})/(\d{1,2})"""),
            ) { match, today ->
                safeDate(today.year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
            },

            Rule(
                regex = Regex("""(\d{1,2})-(\d{1,2})"""),
            ) { match, today ->
                safeDate(today.year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
            },

            Rule(
                regex = Regex("""(\d{1,2})\.(\d{1,2})\.(\d{2})"""),
            ) { match, _ ->
                val year = 2000 + match.groupValues[3].toInt()
                safeDate(year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
            },

            Rule(
                regex = Regex("""в\s+прошл\p{L}*\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val day = day(match.groupValues[1]) ?: return@Rule null
                today.with(TemporalAdjusters.previousOrSame(day))
            },
        )
    }
}