package app.lade.chat.api

import app.lade.draftdata.DraftGoal

data class ParseResult(
    val fields: List<ParsedField>,
    val goals: List<DraftGoal> = emptyList(),
    val remaining: String,
    val questions: List<RuleQuestion> = emptyList(),
)