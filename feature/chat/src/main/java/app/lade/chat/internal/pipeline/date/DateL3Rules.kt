package app.lade.chat.internal.pipeline.date

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal class DateL3Rules(
    private val today: LocalDate,
) : ParseRule {

    override val priority: Int = Priority.L3

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        for (rule in RULES) {
            val match = rule.regex.find(raw) ?: continue
            val resolved = rule.resolve(match, today) ?: continue
            val filtered = resolved.filterNot { state.contains(it.first) }
            if (filtered.isEmpty()) continue
            return filtered.map { (key, date) ->
                RuleMatch(
                    key = key,
                    value = FieldValue.Date(date),
                    match = match.value,
                    span = match.range,
                )
            }
        }

        if (!state.contains(FieldKey.DATE_FROM)) {
            val words = Regex("""[а-яё]+""", RegexOption.IGNORE_CASE).findAll(raw)
            for (word in words) {
                val day = day(word.value) ?: continue
                val covered = state.fields.any { it.match.contains(word.value, ignoreCase = true) }
                if (covered) continue
                val date = today.with(TemporalAdjusters.nextOrSame(day))
                return listOf(
                    RuleMatch(
                        key = FieldKey.DATE_FROM,
                        value = FieldValue.Date(date),
                        match = word.value,
                        span = word.range,
                    ),
                )
            }
        }

        return emptyList()
    }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult, LocalDate) -> List<Pair<FieldKey, LocalDate>>?,
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
                listOf(FieldKey.DATE_FROM to today.with(TemporalAdjusters.next(day)))
            },

            Rule(
                regex = Regex("""(\d{1,2})/(\d{1,2})"""),
            ) { match, today ->
                val date = safeDate(today.year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
                    ?: return@Rule null
                listOf(FieldKey.DATE_FROM to date)
            },

            Rule(
                regex = Regex("""(\d{1,2})-(\d{1,2})"""),
            ) { match, today ->
                val date = safeDate(today.year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
                    ?: return@Rule null
                listOf(FieldKey.DATE_FROM to date)
            },

            Rule(
                regex = Regex("""(\d{1,2})\.(\d{1,2})\.(\d{2})"""),
            ) { match, _ ->
                val year = 2000 + match.groupValues[3].toInt()
                val date = safeDate(year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
                    ?: return@Rule null
                listOf(FieldKey.DATE_FROM to date)
            },

            Rule(
                regex = Regex("""в\s+прошл\p{L}*\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val day = day(match.groupValues[1]) ?: return@Rule null
                listOf(FieldKey.DATE_FROM to today.with(TemporalAdjusters.previousOrSame(day)))
            },
        )
    }
}