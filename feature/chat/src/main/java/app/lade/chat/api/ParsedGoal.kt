package app.lade.chat.api

import app.lade.draftdata.DraftGoal

data class ParsedGoal(
    val label: String?,
    val goal: DraftGoal,
    val modifier: GoalModifier? = null,
)