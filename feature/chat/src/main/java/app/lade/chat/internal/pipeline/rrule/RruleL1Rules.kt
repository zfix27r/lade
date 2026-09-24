package app.lade.chat.internal.pipeline.rrule

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority

internal class RruleL1Rules : ParseRule {

    override val priority: Int = Priority.L1

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        for ((word, rrule) in WORDS) {
            val index = raw.indexOf(word)
            if (index < 0) continue
            if (state.contains(FieldKey.RRULE)) continue
            return listOf(
                RuleMatch(
                    key = FieldKey.RRULE,
                    value = FieldValue.Text(rrule),
                    match = word,
                    span = index until index + word.length,
                ),
            )
        }
        return emptyList()
    }

    companion object {
        private val WORDS: List<Pair<String, String>> = listOf(
            "ежедневно" to "FREQ=DAILY",
            "каждый день" to "FREQ=DAILY",
            "по будням" to "FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR",
            "по выходным" to "FREQ=WEEKLY;BYDAY=SA,SU",
            "еженедельно" to "FREQ=WEEKLY",
            "каждую неделю" to "FREQ=WEEKLY",
            "ежемесячно" to "FREQ=MONTHLY",
            "каждый месяц" to "FREQ=MONTHLY",
            "ежегодно" to "FREQ=YEARLY",
            "каждый год" to "FREQ=YEARLY",
        )
    }
}