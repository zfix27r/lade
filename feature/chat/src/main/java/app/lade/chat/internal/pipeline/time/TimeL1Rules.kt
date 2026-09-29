package app.lade.chat.internal.pipeline.time

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.pipeline.ParseRule
import app.lade.chat.internal.pipeline.Priority
import app.lade.chat.internal.pipeline.RuleResult
import java.time.LocalTime

internal class TimeL1Rules : ParseRule {

    override val priority: Int = Priority.L1

    override fun apply(model: ParserModel, remaining: String): RuleResult {
        val entry = model.entry ?: return RuleResult(model, remaining)
        if (!ParserContract.isFind(entry.timeFrom)) return RuleResult(model, remaining)

        for ((word, time) in WORDS) {
            val index = remaining.indexOf(word)
            if (index < 0) continue
            return RuleResult(
                model.copy(entry = entry.copy(timeFrom = ParserContract.found(time.toString()))),
                remaining.removeRange(index, index + word.length),
            )
        }
        return RuleResult(model, remaining)
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