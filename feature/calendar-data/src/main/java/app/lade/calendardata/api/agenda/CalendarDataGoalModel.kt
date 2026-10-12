package app.lade.calendardata.api.agenda

import app.lade.goal.GoalUnit

data class CalendarDataGoalModel(
    val id: Long,
    val title: String,
    val isDone: Boolean,
    val actualAmount: Int?,
    val plannedAmount: Int?,
    val unit: GoalUnit,
    val repeat: Int?,
    val weight: Double?,
)