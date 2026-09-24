package app.lade.chat.internal.state

import app.lade.chat.api.FieldKey
import app.lade.chat.api.ParseResult
import app.lade.chat.api.ParsedField
import app.lade.chat.api.RuleMatch
import app.lade.chat.api.RuleQuestion

internal class ParsePipeline(
    private val rules: List<ParseRule>,
) {
    fun run(raw: String, initial: ParseState): ParseResult {
        var state = initial
        val questions = mutableListOf<RuleQuestion>()

        state = prune(state, raw)

        var remaining = raw

        for (priority in listOf(Priority.L1, Priority.L2, Priority.L3, Priority.L4)) {
            for (rule in rules.filter { it.priority == priority }) {
                val currentLower = remaining.lowercase()

                val ruleQuestions = rule.question(currentLower, state)
                    .filterNot { state.contains(it.key) && it.key != FieldKey.GOAL }
                if (ruleQuestions.isNotEmpty()) {
                    questions += ruleQuestions
                    continue
                }

                val matches = rule.match(currentLower, state)
                for (match in matches) {
                    if (match.key != FieldKey.GOAL && state.contains(match.key)) continue
                    state = state.add(
                        ParsedField(
                            key = match.key,
                            value = match.value,
                            match = match.match,
                        ),
                    )
                    remaining = removeMatch(remaining, match.match)
                }
            }
        }

        return ParseResult(
            fields = state.fields,
            remaining = remaining.replace(Regex("\\s+"), " ").trim(),
            questions = questions,
        )
    }

    private fun removeMatch(source: String, match: String): String {
        val index = source.lowercase().indexOf(match.lowercase())
        if (index < 0) return source
        return source.removeRange(index, index + match.length)
    }

    private fun prune(state: ParseState, raw: String): ParseState {
        val lower = raw.lowercase()
        return state.copy(fields = state.fields.filter { lower.contains(it.match.lowercase()) })
    }
}