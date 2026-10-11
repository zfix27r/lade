package app.lade.parser.internal.time

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRuleResult
import java.time.LocalTime

internal class ParserTimeL1Rules : ParserRule {

    override val priority: Int = ParserPriority.L1

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)
        if (!ParserContract.isFind(entry.timeFrom)) return ParserRuleResult(model, remaining)

        for ((word, time) in WORDS) {
            val index = remaining.indexOf(word)
            if (index < 0) continue
            return ParserRuleResult(
                model.copy(entry = entry.copy(timeFrom = ParserContract.found(time.toString()))),
                remaining.removeRange(index, index + word.length),
            )
        }
        return ParserRuleResult(model, remaining)
    }

    companion object {
        private val WORDS: List<Pair<String, LocalTime>> = listOf(
            "по утрам" to LocalTime.of(9, 0),
            "по вечерам" to LocalTime.of(18, 0),
            "по ночам" to LocalTime.of(22, 0),
            "по дням" to LocalTime.of(12, 0),
            "утром" to LocalTime.of(9, 0),
            "днём" to LocalTime.of(12, 0),
            "днем" to LocalTime.of(12, 0),
            "вечером" to LocalTime.of(18, 0),
            "ночью" to LocalTime.of(22, 0),
            "в полдень" to LocalTime.of(12, 0),
            "в полночь" to LocalTime.of(0, 0),
        )
    }
}