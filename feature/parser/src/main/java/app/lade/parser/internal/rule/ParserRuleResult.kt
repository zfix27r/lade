package app.lade.parser.internal.rule

import app.lade.parser.api.ParserModel

internal data class ParserRuleResult(
    val model: ParserModel,
    val remaining: String,
)