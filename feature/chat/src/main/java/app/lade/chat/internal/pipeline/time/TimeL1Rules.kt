package app.lade.chat.internal.pipeline.time

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import java.time.LocalTime

internal class TimeL1Rules : ParseRule {

    override val priority: Int = Priority.L1

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        for ((word, time) in WORDS) {
            val index = raw.indexOf(word)
            if (index < 0) continue
            return listOf(
                RuleMatch(
                    key = FieldKey.TIME_FROM,
                    value = FieldValue.Time(time),
                    match = word,
                    span = index until index + word.length,
                ),
            )
        }
        return emptyList()
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