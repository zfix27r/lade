package app.lade.chat.internal.state

import app.lade.chat.api.RuleMatch
import app.lade.chat.api.RuleQuestion

internal interface ParseRule {
    val priority: Int

    fun match(raw: String, state: ParseState): List<RuleMatch>

    fun question(raw: String, state: ParseState): List<RuleQuestion> = emptyList()
}