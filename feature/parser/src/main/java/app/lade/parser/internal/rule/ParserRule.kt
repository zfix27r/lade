package app.lade.parser.internal.rule

import app.lade.parser.api.ParserModel

internal interface ParserRule {
    val priority: Int
    fun apply(model: ParserModel, remaining: String): ParserRuleResult
}