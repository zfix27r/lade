package app.lade.chat.internal.pipeline.rrule

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.RuleMatch
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority

internal class RruleL2Rules : ParseRule {

    override val priority: Int = Priority.L2

    override fun match(raw: String, state: ParseState): List<RuleMatch> {
        if (state.contains(FieldKey.RRULE)) return emptyList()
        for (rule in RULES) {
            val match = rule.regex.find(raw) ?: continue
            val rrule = rule.resolve(match) ?: continue
            return listOf(
                RuleMatch(
                    key = FieldKey.RRULE,
                    value = FieldValue.Text(rrule),
                    match = match.value,
                    span = match.range,
                ),
            )
        }
        return emptyList()
    }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult) -> String?,
    )

    companion object {
        private val DAYS: Map<String, String> = mapOf(
            "понедельник" to "MO",
            "вторник" to "TU",
            "сред" to "WE",
            "четверг" to "TH",
            "пятниц" to "FR",
            "суббот" to "SA",
            "воскрес" to "SU",
        )

        private val ORDINALS: Map<String, Int> = mapOf(
            "второй" to 2,
            "третий" to 3,
            "четвёртый" to 4,
            "пятый" to 5,
        )

        private fun dayOf(word: String): String? =
            DAYS.entries.firstOrNull { word.startsWith(it.key) }?.value

        private val RULES: List<Rule> = listOf(
            Rule(
                regex = Regex("""кажд\p{L}+\s+([а-яё]+)\s+и\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match ->
                val first = dayOf(match.groupValues[1]) ?: return@Rule null
                val second = dayOf(match.groupValues[2]) ?: return@Rule null
                "FREQ=WEEKLY;BYDAY=$first,$second"
            },

            Rule(
                regex = Regex("""кажд\p{L}+\s+([а-яё]+)\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match ->
                val ordinal = ORDINALS[match.groupValues[1].lowercase()] ?: return@Rule null
                val day = dayOf(match.groupValues[2]) ?: return@Rule null
                "FREQ=WEEKLY;INTERVAL=$ordinal;BYDAY=$day"
            },

            Rule(
                regex = Regex("""кажд\p{L}+\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match ->
                val day = dayOf(match.groupValues[1]) ?: return@Rule null
                "FREQ=WEEKLY;BYDAY=$day"
            },

            Rule(
                regex = Regex("""по\s+([а-яё]+)\s+и\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match ->
                val first = dayOf(match.groupValues[1]) ?: return@Rule null
                val second = dayOf(match.groupValues[2]) ?: return@Rule null
                "FREQ=WEEKLY;BYDAY=$first,$second"
            },

            Rule(
                regex = Regex("""по\s+([а-яё]+)""", RegexOption.IGNORE_CASE),
            ) { match ->
                val day = dayOf(match.groupValues[1]) ?: return@Rule null
                "FREQ=WEEKLY;BYDAY=$day"
            },

            Rule(
                regex = Regex("""раз\s+в\s+(\d+)\s+дн\p{L}*""", RegexOption.IGNORE_CASE),
            ) { match ->
                "FREQ=DAILY;INTERVAL=${match.groupValues[1].toInt()}"
            },

            Rule(
                regex = Regex("""раз\s+в\s+(\d+)\s+недел\p{L}*""", RegexOption.IGNORE_CASE),
            ) { match ->
                "FREQ=WEEKLY;INTERVAL=${match.groupValues[1].toInt()}"
            },

            Rule(
                regex = Regex("""раз\s+в\s+(\d+)\s+месяц\p{L}*""", RegexOption.IGNORE_CASE),
            ) { match ->
                "FREQ=MONTHLY;INTERVAL=${match.groupValues[1].toInt()}"
            },

            Rule(
                regex = Regex("""каждые\s+(\d+)\s+дн\p{L}*""", RegexOption.IGNORE_CASE),
            ) { match ->
                "FREQ=DAILY;INTERVAL=${match.groupValues[1].toInt()}"
            },

            Rule(
                regex = Regex("""каждые\s+(\d+)\s+недел\p{L}*""", RegexOption.IGNORE_CASE),
            ) { match ->
                "FREQ=WEEKLY;INTERVAL=${match.groupValues[1].toInt()}"
            },

            Rule(
                regex = Regex("""раз\s+в\s+месяц""", RegexOption.IGNORE_CASE),
            ) { _ ->
                "FREQ=MONTHLY"
            },
        )
    }
}