package app.lade.chat.internal.pipeline.date

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import app.lade.chat.api.RuleMatch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal class DateL1WeekendRule(
    private val today: LocalDate,
) : ParseRule {

    override val priority: Int = Priority.L1

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        for (word in WORDS) {
            val index = raw.indexOf(word)
            if (index < 0) continue
            val saturday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
            val sunday = saturday.plusDays(1)
            return listOf(
                RuleMatch(
                    key = FieldKey.DATE_FROM,
                    value = FieldValue.Date(saturday),
                    match = word,
                    span = index until index + word.length,
                ),
                RuleMatch(
                    key = FieldKey.DATE_TO,
                    value = FieldValue.Date(sunday),
                    match = word,
                    span = index until index + word.length,
                ),
            )
        }
        return emptyList()
    }

    companion object {
        private val WORDS = listOf("на выходные", "в выходные")
    }
}