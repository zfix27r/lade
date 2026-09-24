package app.lade.chat.internal.pipeline.date

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.TemporalAdjusters

internal class DateL2Rules(
    private val today: LocalDate,
) : ParseRule {

    override val priority: Int = Priority.L2

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
        return emptyList()
    }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult, LocalDate) -> List<Pair<FieldKey, LocalDate>>?,
    )

    companion object {
        private val MONTHS: List<Pair<Regex, Month>> = listOf(
            Regex("""январ[ьяе]""", RegexOption.IGNORE_CASE) to Month.JANUARY,
            Regex("""феврал[ьяе]""", RegexOption.IGNORE_CASE) to Month.FEBRUARY,
            Regex("""март[ае]?""", RegexOption.IGNORE_CASE) to Month.MARCH,
            Regex("""апрел[ьяе]""", RegexOption.IGNORE_CASE) to Month.APRIL,
            Regex("""ма[йяе]""", RegexOption.IGNORE_CASE) to Month.MAY,
            Regex("""июн[ьяе]""", RegexOption.IGNORE_CASE) to Month.JUNE,
            Regex("""июл[ьяе]""", RegexOption.IGNORE_CASE) to Month.JULY,
            Regex("""август[ае]?""", RegexOption.IGNORE_CASE) to Month.AUGUST,
            Regex("""сентябр[ьяе]""", RegexOption.IGNORE_CASE) to Month.SEPTEMBER,
            Regex("""октябр[ьяе]""", RegexOption.IGNORE_CASE) to Month.OCTOBER,
            Regex("""ноябр[ьяе]""", RegexOption.IGNORE_CASE) to Month.NOVEMBER,
            Regex("""декабр[ьяе]""", RegexOption.IGNORE_CASE) to Month.DECEMBER,
        )

        private val MONTH_PATTERN = MONTHS.joinToString("|") { it.first.pattern }

        private val DAYS: List<Pair<Regex, DayOfWeek>> = listOf(
            Regex("""пон?едельник\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.MONDAY,
            Regex("""вторник\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.TUESDAY,
            Regex("""сред[аыу]\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.WEDNESDAY,
            Regex("""четверг\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.THURSDAY,
            Regex("""пятниц[аыу]\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.FRIDAY,
            Regex("""суббот[аыу]\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.SATURDAY,
            Regex("""воскресень[ея]\p{L}*""", RegexOption.IGNORE_CASE) to DayOfWeek.SUNDAY,
        )

        private fun month(text: String): Month? =
            MONTHS.firstOrNull { it.first.matches(text) }?.second

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
                regex = Regex("""(\d{1,2})\.(\d{1,2})\s+и\s+(\d{1,2})\.(\d{1,2})"""),
            ) { match, today ->
                val from = safeDate(today.year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
                    ?: return@Rule null
                val to = safeDate(today.year, match.groupValues[4].toInt(), match.groupValues[3].toInt())
                    ?: return@Rule null
                listOf(FieldKey.DATE_FROM to from, FieldKey.DATE_TO to to)
            },

            Rule(
                regex = Regex("""\d{1,2}\.\d{1,2}\.\d{4}"""),
            ) { match, _ ->
                val parts = match.value.split(".")
                safeDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                    ?.let { listOf(FieldKey.DATE_FROM to it) }
            },

            Rule(
                regex = Regex("""\d{1,2}\.\d{1,2}"""),
            ) { match, today ->
                val parts = match.value.split(".")
                safeDate(today.year, parts[1].toInt(), parts[0].toInt())
                    ?.let { listOf(FieldKey.DATE_FROM to it) }
            },

            Rule(
                regex = Regex("""до\s+(\d{1,2})\.(\d{1,2})"""),
            ) { match, today ->
                val date =
                    safeDate(today.year, match.groupValues[2].toInt(), match.groupValues[1].toInt())
                        ?: return@Rule null
                listOf(FieldKey.DATE_FROM to today, FieldKey.DATE_TO to date)
            },

            Rule(
                regex = Regex("""до\s+(\d{1,2})\s+($MONTH_PATTERN)""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val day = match.groupValues[1].toInt()
                val month = month(match.groupValues[2]) ?: return@Rule null
                val date = safeDate(today.year, month.value, day) ?: return@Rule null
                listOf(FieldKey.DATE_FROM to today, FieldKey.DATE_TO to date)
            },

            Rule(
                regex = Regex(
                    """с\s+(\d{1,2})\s+по\s+(\d{1,2})\s+($MONTH_PATTERN)""",
                    RegexOption.IGNORE_CASE
                ),
            ) { match, today ->
                val month = month(match.groupValues[3]) ?: return@Rule null
                val from = safeDate(today.year, month.value, match.groupValues[1].toInt())
                    ?: return@Rule null
                val to = safeDate(today.year, month.value, match.groupValues[2].toInt())
                    ?: return@Rule null
                listOf(FieldKey.DATE_FROM to from, FieldKey.DATE_TO to to)
            },

            Rule(
                regex = Regex(
                    """(\d{1,2})\s*[–-]\s*(\d{1,2})\s+($MONTH_PATTERN)""",
                    RegexOption.IGNORE_CASE
                ),
            ) { match, today ->
                val month = month(match.groupValues[3]) ?: return@Rule null
                val from = safeDate(today.year, month.value, match.groupValues[1].toInt())
                    ?: return@Rule null
                val to = safeDate(today.year, month.value, match.groupValues[2].toInt())
                    ?: return@Rule null
                listOf(FieldKey.DATE_FROM to from, FieldKey.DATE_TO to to)
            },

            Rule(
                regex = Regex("""(\d{1,2})\s+($MONTH_PATTERN)""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val day = match.groupValues[1].toInt()
                val month = month(match.groupValues[2]) ?: return@Rule null
                safeDate(today.year, month.value, day)
                    ?.let { listOf(FieldKey.DATE_FROM to it) }
            },

            Rule(
                regex = Regex("""до\s+конца\s+недели""", RegexOption.IGNORE_CASE),
            ) { _, today ->
                val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                listOf(FieldKey.DATE_FROM to today, FieldKey.DATE_TO to sunday)
            },

            Rule(
                regex = Regex("""до\s+конца\s+месяца""", RegexOption.IGNORE_CASE),
            ) { _, today ->
                val last = today.with(TemporalAdjusters.lastDayOfMonth())
                listOf(FieldKey.DATE_FROM to today, FieldKey.DATE_TO to last)
            },

            Rule(
                regex = Regex("""до\s+конца\s+года""", RegexOption.IGNORE_CASE),
            ) { _, today ->
                val last = LocalDate.of(today.year, 12, 31)
                listOf(FieldKey.DATE_FROM to today, FieldKey.DATE_TO to last)
            },

            Rule(
                regex = Regex("""в[оа]?\s+следующий\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val day = day(match.groupValues[1]) ?: return@Rule null
                listOf(FieldKey.DATE_FROM to today.with(TemporalAdjusters.next(day)))
            },

            Rule(
                regex = Regex(
                    """в[оа]?\s+(пон?едельник\p{L}*|вторник\p{L}*|сред[аыу]\p{L}*|четверг\p{L}*|пятниц[аыу]\p{L}*|суббот[аыу]\p{L}*|воскресень[ея]\p{L}*)""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, today ->
                val day = day(match.groupValues[1]) ?: return@Rule null
                listOf(FieldKey.DATE_FROM to today.with(TemporalAdjusters.nextOrSame(day)))
            },
        )
    }
}