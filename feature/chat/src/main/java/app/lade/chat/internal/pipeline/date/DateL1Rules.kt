package app.lade.chat.internal.pipeline.date

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import app.lade.chat.api.RuleMatch
import java.time.LocalDate

internal class DateL1Rules(
    private val today: LocalDate,
) : ParseRule {

    override val priority: Int = Priority.L1

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        for ((word, resolver) in WORDS) {
            val index = findWord(raw, word) ?: continue
            return listOf(
                RuleMatch(
                    key = FieldKey.DATE_FROM,
                    value = FieldValue.Date(resolver(today)),
                    match = word,
                    span = index until index + word.length,
                ),
            )
        }
        return emptyList()
    }

    private fun findWord(raw: String, word: String): Int? {
        var start = 0
        while (true) {
            val index = raw.indexOf(word, start)
            if (index < 0) return null
            val before = raw.getOrNull(index - 1)
            val after = raw.getOrNull(index + word.length)
            val isLetterBefore = before?.isLetter() == true
            val isLetterAfter = after?.isLetter() == true
            if (!isLetterBefore && !isLetterAfter) return index
            start = index + 1
        }
    }

    companion object {
        private val WORDS: List<Pair<String, (LocalDate) -> LocalDate>> = listOf(
            "послезавтра" to { it.plusDays(2) },
            "позавчера" to { it.minusDays(2) },
            "сегодня" to { it },
            "завтра" to { it.plusDays(1) },
            "вчера" to { it.minusDays(1) },
        )
    }
}