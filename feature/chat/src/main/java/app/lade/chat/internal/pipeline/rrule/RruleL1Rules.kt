package app.lade.chat.internal.pipeline.rrule

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.pipeline.ParseRule
import app.lade.chat.internal.pipeline.Priority
import app.lade.chat.internal.pipeline.RuleResult

internal class RruleL1Rules : ParseRule {

    override val priority: Int = Priority.L1

    override fun apply(model: ParserModel, remaining: String): RuleResult {
        val entry = model.entry ?: return RuleResult(model, remaining)
        if (!ParserContract.isFind(entry.rrule)) return RuleResult(model, remaining)

        for ((word, rrule) in WORDS) {
            val index = remaining.indexOf(word)
            if (index < 0) continue
            return RuleResult(
                model.copy(entry = entry.copy(rrule = ParserContract.found(rrule))),
                remaining.removeRange(index, index + word.length),
            )
        }
        return RuleResult(model, remaining)
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