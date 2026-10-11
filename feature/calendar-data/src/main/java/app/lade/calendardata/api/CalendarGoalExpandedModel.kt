package app.lade.calendardata.api

import app.lade.goal.GoalUnit

data class CalendarGoalExpandedModel(
    val id: Long,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val actualAmount: Int?,
    val plannedAmount: Int?,
    val unit: GoalUnit,
    val repeat: Int?,
    val weight: Double?,
)