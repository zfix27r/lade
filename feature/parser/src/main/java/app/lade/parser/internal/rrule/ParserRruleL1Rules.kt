package app.lade.parser.internal.rrule

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult

internal class ParserRruleL1Rules : ParserRule {

    override val priority: Int = ParserPriority.L1

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)
        if (!ParserContract.isFind(entry.rrule)) return ParserRuleResult(model, remaining)

        for ((word, rrule) in WORDS) {
            val index = remaining.indexOf(word)
            if (index < 0) continue
            return ParserRuleResult(
                model.copy(entry = entry.copy(rrule = ParserContract.found(rrule))),
                remaining.removeRange(index, index + word.length),
            )
        }
        return ParserRuleResult(model, remaining)
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