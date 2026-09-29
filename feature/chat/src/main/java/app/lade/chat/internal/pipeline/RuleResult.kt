package app.lade.chat.internal.pipeline

import app.lade.chat.api.ParserModel

internal data class RuleResult(
    val model: ParserModel,
    val remaining: String,
)