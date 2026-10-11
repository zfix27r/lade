package app.lade.parser.internal.date

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal class DateL1WeekendRule(
    private val today: LocalDate,
) : ParserRule {

    override val priority: Int = ParserPriority.L1

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)
        val findFrom = ParserContract.isFind(entry.dateFrom)
        val findTo = ParserContract.isFind(entry.dateTo)
        if (!findFrom && !findTo) return ParserRuleResult(model, remaining)

        for (word in WORDS) {
            val index = remaining.indexOf(word)
            if (index < 0) continue

            val saturday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
            val sunday = saturday.plusDays(1)

            val updated = model.copy(
                entry = entry.copy(
                    dateFrom = if (findFrom) ParserContract.found(saturday.toString()) else entry.dateFrom,
                    dateTo = if (findTo) ParserContract.found(sunday.toString()) else entry.dateTo,
                ),
            )
            return ParserRuleResult(updated, remaining.removeRange(index, index + word.length))
        }
        return ParserRuleResult(model, remaining)
    }

    companion object {
        private val WORDS = listOf("на выходные", "в выходные")
    }
}