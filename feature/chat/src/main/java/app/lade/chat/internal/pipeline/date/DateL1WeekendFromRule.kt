package app.lade.chat.internal.pipeline.date

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.pipeline.ParseRule
import app.lade.chat.internal.pipeline.Priority
import app.lade.chat.internal.pipeline.RuleResult
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal class DateL1WeekendRule(
    private val today: LocalDate,
) : ParseRule {

    override val priority: Int = Priority.L1

    override fun apply(model: ParserModel, remaining: String): RuleResult {
        val entry = model.entry ?: return RuleResult(model, remaining)
        val findFrom = ParserContract.isFind(entry.dateFrom)
        val findTo = ParserContract.isFind(entry.dateTo)
        if (!findFrom && !findTo) return RuleResult(model, remaining)

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
            return RuleResult(updated, remaining.removeRange(index, index + word.length))
        }
        return RuleResult(model, remaining)
    }

    companion object {
        private val WORDS = listOf("на выходные", "в выходные")
    }
}