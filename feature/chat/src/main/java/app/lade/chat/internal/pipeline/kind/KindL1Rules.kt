package app.lade.chat.internal.pipeline.kind

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.pipeline.ParseRule
import app.lade.chat.internal.pipeline.Priority
import app.lade.chat.internal.pipeline.RuleResult

internal class KindL1Rules : ParseRule {

    override val priority: Int = Priority.L1

    override fun apply(model: ParserModel, remaining: String): RuleResult {
        val entry = model.entry ?: return RuleResult(model, remaining)
        if (!ParserContract.isFind(entry.kind)) return RuleResult(model, remaining)

        for ((word, kind) in WORDS) {
            val index = remaining.indexOf(word)
            if (index < 0) continue
            return RuleResult(
                model.copy(entry = entry.copy(kind = ParserContract.found(kind))),
                remaining.removeRange(index, index + word.length),
            )
        }
        return RuleResult(model, remaining)
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