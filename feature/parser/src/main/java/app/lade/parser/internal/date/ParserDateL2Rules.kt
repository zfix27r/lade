package app.lade.parser.internal.date

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.TemporalAdjusters

internal class ParserDateL2Rules(
    private val today: LocalDate,
) : ParserRule {

    override val priority: Int = ParserPriority.L2

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)

        for (rule in RULES) {
            val match = rule.regex.find(remaining) ?: continue
            val resolved = rule.resolve(match, today) ?: continue

            val applicable = resolved.filter { (field) ->
                when (field) {
                    Field.DATE_FROM -> ParserContract.isFind(entry.dateFrom)
                    Field.DATE_TO -> ParserContract.isFind(entry.dateTo)
                }
            }
            if (applicable.isEmpty()) continue

            var updated = entry
            applicable.forEach { (field, date) ->
                updated = when (field) {
                    Field.DATE_FROM -> updated.copy(dateFrom = ParserContract.found(date.toString()))
                    Field.DATE_TO -> updated.copy(dateTo = ParserContract.found(date.toString()))
                }
            }

            return ParserRuleResult(
                model.copy(entry = updated),
                remaining.removeRange(match.range),
            )
        }
        return ParserRuleResult(model, remaining)
    }

    private enum class Field { DATE_FROM, DATE_TO }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult, LocalDate) -> List<Pair<Field, LocalDate>>?,
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

        private fun adjustYear(from: LocalDate, to: LocalDate): LocalDate =
            if (to.isBefore(from)) to.plusYears(1) else to

        private const val DASH = """\s*[–\-—]\s*"""

        private val RULES: List<Rule> = listOf(

            Rule(
                regex = Regex(
                    """(\d{1,2})\.(\d{1,2})\.(\d{4})$DASH(\d{1,2})\.(\d{1,2})\.(\d{4})"""
                ),
            ) { match, _ ->
                val from = safeDate(
                    match.groupValues[3].toInt(),
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                ) ?: return@Rule null
                val to = safeDate(
                    match.groupValues[6].toInt(),
                    match.groupValues[5].toInt(),
                    match.groupValues[4].toInt(),
                ) ?: return@Rule null
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex(
                    """с\s+(\d{1,2})\.(\d{1,2})\.(\d{4})\s+(?:по|до)\s+(\d{1,2})\.(\d{1,2})\.(\d{4})""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, _ ->
                val from = safeDate(
                    match.groupValues[3].toInt(),
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                ) ?: return@Rule null
                val to = safeDate(
                    match.groupValues[6].toInt(),
                    match.groupValues[5].toInt(),
                    match.groupValues[4].toInt(),
                ) ?: return@Rule null
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex("""(\d{1,2})\.(\d{1,2})$DASH(\d{1,2})\.(\d{1,2})"""),
            ) { match, today ->
                val from = safeDate(
                    today.year,
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                ) ?: return@Rule null
                val toRaw = safeDate(
                    today.year,
                    match.groupValues[4].toInt(),
                    match.groupValues[3].toInt(),
                ) ?: return@Rule null
                val to = adjustYear(from, toRaw)
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex(
                    """с\s+(\d{1,2})\.(\d{1,2})\s+(?:по|до)\s+(\d{1,2})\.(\d{1,2})""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, today ->
                val from = safeDate(
                    today.year,
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                ) ?: return@Rule null
                val toRaw = safeDate(
                    today.year,
                    match.groupValues[4].toInt(),
                    match.groupValues[3].toInt(),
                ) ?: return@Rule null
                val to = adjustYear(from, toRaw)
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex("""(\d{1,2})\.(\d{1,2})\s+и\s+(\d{1,2})\.(\d{1,2})"""),
            ) { match, today ->
                val from = safeDate(
                    today.year,
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                ) ?: return@Rule null
                val toRaw = safeDate(
                    today.year,
                    match.groupValues[4].toInt(),
                    match.groupValues[3].toInt(),
                ) ?: return@Rule null
                val to = adjustYear(from, toRaw)
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex(
                    """с\s+(\d{1,2})\s+($MONTH_PATTERN)\s+по\s+(\d{1,2})\s+($MONTH_PATTERN)""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, today ->
                val monthFrom = month(match.groupValues[2]) ?: return@Rule null
                val monthTo = month(match.groupValues[4]) ?: return@Rule null
                val from = safeDate(today.year, monthFrom.value, match.groupValues[1].toInt())
                    ?: return@Rule null
                val toRaw = safeDate(today.year, monthTo.value, match.groupValues[3].toInt())
                    ?: return@Rule null
                val to = adjustYear(from, toRaw)
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex(
                    """с\s+(\d{1,2})\s+по\s+(\d{1,2})\s+($MONTH_PATTERN)""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, today ->
                val monthVal = month(match.groupValues[3]) ?: return@Rule null
                val from = safeDate(today.year, monthVal.value, match.groupValues[1].toInt())
                    ?: return@Rule null
                val toRaw = safeDate(today.year, monthVal.value, match.groupValues[2].toInt())
                    ?: return@Rule null
                val to = adjustYear(from, toRaw)
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex(
                    """(\d{1,2})\s+($MONTH_PATTERN)$DASH(\d{1,2})\s+($MONTH_PATTERN)""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, today ->
                val monthFrom = month(match.groupValues[2]) ?: return@Rule null
                val monthTo = month(match.groupValues[4]) ?: return@Rule null
                val from = safeDate(today.year, monthFrom.value, match.groupValues[1].toInt())
                    ?: return@Rule null
                val toRaw = safeDate(today.year, monthTo.value, match.groupValues[3].toInt())
                    ?: return@Rule null
                val to = adjustYear(from, toRaw)
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex(
                    """(\d{1,2})$DASH(\d{1,2})\s+($MONTH_PATTERN)""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, today ->
                val monthVal = month(match.groupValues[3]) ?: return@Rule null
                val from = safeDate(today.year, monthVal.value, match.groupValues[1].toInt())
                    ?: return@Rule null
                val toRaw = safeDate(today.year, monthVal.value, match.groupValues[2].toInt())
                    ?: return@Rule null
                val to = adjustYear(from, toRaw)
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex("""(?<![\d.])(\d{1,2})$DASH(\d{1,2})(?![\d.])"""),
            ) { match, today ->
                val fromDay = match.groupValues[1].toInt()
                val toDay = match.groupValues[2].toInt()
                if (fromDay !in 1..31 || toDay !in 1..31) return@Rule null
                val from = safeDate(today.year, today.monthValue, fromDay)
                    ?: return@Rule null
                val toRaw = safeDate(today.year, today.monthValue, toDay)
                    ?: return@Rule null
                val to = adjustYear(from, toRaw)
                listOf(Field.DATE_FROM to from, Field.DATE_TO to to)
            },

            Rule(
                regex = Regex("""до\s+(\d{1,2})\.(\d{1,2})\.(\d{4})""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val date = safeDate(
                    match.groupValues[3].toInt(),
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                ) ?: return@Rule null
                listOf(Field.DATE_FROM to today, Field.DATE_TO to date)
            },

            Rule(
                regex = Regex("""до\s+(\d{1,2})\.(\d{1,2})(?![\d.])""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val date = safeDate(
                    today.year,
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                ) ?: return@Rule null
                listOf(Field.DATE_FROM to today, Field.DATE_TO to date)
            },

            Rule(
                regex = Regex(
                    """до\s+(\d{1,2})\s+($MONTH_PATTERN)""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, today ->
                val dayVal = match.groupValues[1].toInt()
                val monthVal = month(match.groupValues[2]) ?: return@Rule null
                val date = safeDate(today.year, monthVal.value, dayVal) ?: return@Rule null
                listOf(Field.DATE_FROM to today, Field.DATE_TO to date)
            },

            Rule(
                regex = Regex("""(?<![\d.])(\d{1,2})\.(\d{1,2})\.(\d{4})(?![\d.])"""),
            ) { match, _ ->
                safeDate(
                    match.groupValues[3].toInt(),
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                )?.let { listOf(Field.DATE_FROM to it) }
            },

            Rule(
                regex = Regex("""(?<![\d.])(\d{1,2})\.(\d{1,2})(?![\d.])"""),
            ) { match, today ->
                safeDate(
                    today.year,
                    match.groupValues[2].toInt(),
                    match.groupValues[1].toInt(),
                )?.let { listOf(Field.DATE_FROM to it) }
            },

            Rule(
                regex = Regex("""(?<![\d.])(\d{1,2})\s+($MONTH_PATTERN)""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val dayVal = match.groupValues[1].toInt()
                val monthVal = month(match.groupValues[2]) ?: return@Rule null
                safeDate(today.year, monthVal.value, dayVal)
                    ?.let { listOf(Field.DATE_FROM to it) }
            },

            Rule(
                regex = Regex("""до\s+конца\s+недели""", RegexOption.IGNORE_CASE),
            ) { _, today ->
                val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                listOf(Field.DATE_FROM to today, Field.DATE_TO to sunday)
            },

            Rule(
                regex = Regex("""до\s+конца\s+месяца""", RegexOption.IGNORE_CASE),
            ) { _, today ->
                val last = today.with(TemporalAdjusters.lastDayOfMonth())
                listOf(Field.DATE_FROM to today, Field.DATE_TO to last)
            },

            Rule(
                regex = Regex("""до\s+конца\s+года""", RegexOption.IGNORE_CASE),
            ) { _, today ->
                val last = LocalDate.of(today.year, 12, 31)
                listOf(Field.DATE_FROM to today, Field.DATE_TO to last)
            },

            Rule(
                regex = Regex("""в[оа]?\s+следующий\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match, today ->
                val d = day(match.groupValues[1]) ?: return@Rule null
                listOf(Field.DATE_FROM to today.with(TemporalAdjusters.next(d)))
            },

            Rule(
                regex = Regex(
                    """в[оа]?\s+(пон?едельник\p{L}*|вторник\p{L}*|сред[аыу]\p{L}*|четверг\p{L}*|пятниц[аыу]\p{L}*|суббот[аыу]\p{L}*|воскресень[ея]\p{L}*)""",
                    RegexOption.IGNORE_CASE,
                ),
            ) { match, today ->
                val d = day(match.groupValues[1]) ?: return@Rule null
                listOf(Field.DATE_FROM to today.with(TemporalAdjusters.nextOrSame(d)))
            },
        )
    }
}