package app.lade.parser.internal.kind

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.match.ParserMatchEntry
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult

internal class ParserKindL2Rules(
    private val entries: List<ParserMatchEntry>,
) : ParserRule {

    override val priority: Int = ParserPriority.L2

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)
        if (!ParserContract.isFind(entry.kind)) return ParserRuleResult(model, remaining)

        for (corpus in entries) {
            for (needle in corpus.needles) {
                val stem = needle.stem
                if (stem.length < 3) continue
                if (remaining.indexOf(stem) < 0) continue
                return ParserRuleResult(
                    model.copy(entry = entry.copy(kind = ParserContract.found(corpus.entryKind.storage))),
                    remaining,
                )
            }
        }
        return ParserRuleResult(model, remaining)
    }
}