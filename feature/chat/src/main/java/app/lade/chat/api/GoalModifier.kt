package app.lade.chat.api

import app.lade.agenda.api.goal.GoalUnit

data class GoalModifier(
    val value: Int,
    val unit: GoalUnit,
    val sign: Sign,
) {
    enum class Sign { PLUS, MINUS }
}