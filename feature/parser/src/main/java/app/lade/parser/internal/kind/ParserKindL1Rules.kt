package app.lade.parser.internal.kind

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult

internal class ParserKindL1Rules : ParserRule {

    override val priority: Int = ParserPriority.L1

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)
        if (!ParserContract.isFind(entry.kind)) return ParserRuleResult(model, remaining)

        for ((word, kind) in WORDS) {
            val index = remaining.indexOf(word)
            if (index < 0) continue
            return ParserRuleResult(
                model.copy(entry = entry.copy(kind = ParserContract.found(kind))),
                remaining.removeRange(index, index + word.length),
            )
        }
        return ParserRuleResult(model, remaining)
    }

    companion object {
        private val WORDS: List<Pair<String, String>> = listOf(
            "привычка" to "habit",
            "задача" to "task",
            "событие" to "event",
            "расписание" to "schedule",
        )
    }
}