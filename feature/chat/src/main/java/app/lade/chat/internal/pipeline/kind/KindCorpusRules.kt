package app.lade.chat.internal.pipeline.kind

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.data.CorpusEntry
import app.lade.chat.internal.pipeline.ParseRule
import app.lade.chat.internal.pipeline.Priority
import app.lade.chat.internal.pipeline.RuleResult

internal class KindCorpusRules(
    private val entries: List<CorpusEntry>,
) : ParseRule {

    override val priority: Int = Priority.L2

    override fun apply(model: ParserModel, remaining: String): RuleResult {
        val entry = model.entry ?: return RuleResult(model, remaining)
        if (!ParserContract.isFind(entry.kind)) return RuleResult(model, remaining)

        for (corpus in entries) {
            for (needle in corpus.needles) {
                val stem = needle.stem
                if (stem.length < 3) continue
                if (remaining.indexOf(stem) < 0) continue
                return RuleResult(
                    model.copy(entry = entry.copy(kind = ParserContract.found(corpus.entryKind.storage))),
                    remaining,
                )
            }
        }
        return RuleResult(model, remaining)
    }
}