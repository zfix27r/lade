package app.lade.chat.internal.pipeline

import app.lade.chat.api.ParserModel

internal interface ParseRule {
    val priority: Int
    fun apply(model: ParserModel, remaining: String): RuleResult
}