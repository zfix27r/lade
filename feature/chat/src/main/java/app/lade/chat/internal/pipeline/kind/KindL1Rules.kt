package app.lade.chat.internal.pipeline.kind

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import app.lade.entrykind.EntryKind

internal class KindL1Rules : ParseRule {

    override val priority: Int = Priority.L1

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        if (state.contains(FieldKey.KIND)) return emptyList()
        for ((word, kind) in WORDS) {
            val index = raw.indexOf(word)
            if (index < 0) continue
            return listOf(
                RuleMatch(
                    key = FieldKey.KIND,
                    value = FieldValue.Kind(kind),
                    match = word,
                    span = index until index + word.length,
                ),
            )
        }
        return emptyList()
    }

    companion object {
        private val WORDS: List<Pair<String, EntryKind>> = listOf(
            "привычка" to EntryKind.HABIT,
            "задача" to EntryKind.TASK,
            "событие" to EntryKind.EVENT,
            "расписание" to EntryKind.SCHEDULE,
        )
    }
}