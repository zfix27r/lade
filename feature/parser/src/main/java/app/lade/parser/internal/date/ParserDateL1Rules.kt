package app.lade.parser.internal.date

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult
import java.time.LocalDate

internal class ParserDateL1Rules(
    private val today: LocalDate,
) : ParserRule {

    override val priority: Int = ParserPriority.L1

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)
        if (!ParserContract.isFind(entry.dateFrom)) return ParserRuleResult(model, remaining)

        for ((word, resolver) in WORDS) {
            val index = findWord(remaining, word) ?: continue
            val updated = model.copy(
                entry = entry.copy(dateFrom = ParserContract.found(resolver(today).toString())),
            )
            return ParserRuleResult(updated, remaining.removeRange(index, index + word.length))
        }
        return ParserRuleResult(model, remaining)
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