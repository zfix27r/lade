package app.lade.chat.internal.pipeline.kind

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.data.CorpusEntry
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority

internal class KindCorpusRules(
    private val entries: List<CorpusEntry>,
) : ParseRule {

    override val priority: Int = Priority.L2

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        if (state.contains(FieldKey.KIND)) return emptyList()

        for (entry in entries) {
            for (needle in entry.needles) {
                val stem = needle.stem
                if (stem.length < 3) continue
                val index = raw.indexOf(stem)
                if (index < 0) continue
                return listOf(
                    RuleMatch(
                        key = FieldKey.KIND,
                        value = FieldValue.Kind(entry.kind, entry.systemKey),
                        match = "",
                        span = index until index + stem.length,
                    ),
                )
            }
        }

        return emptyList()
    }
}