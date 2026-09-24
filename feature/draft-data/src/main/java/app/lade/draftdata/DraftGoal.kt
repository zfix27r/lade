package app.lade.draftdata

import app.lade.agenda.api.goal.GoalUnit

data class DraftGoal(
    val id: Long = 0L,
    val title: String = "",
    val unit: GoalUnit = GoalUnit.UNKNOWN,
    val amount: Int? = null,
    val repeat: Int? = null,
    val weight: Double? = null,
) {
    val isValid: Boolean
        get() = title.isNotBlank() && unit != GoalUnit.UNKNOWN
}