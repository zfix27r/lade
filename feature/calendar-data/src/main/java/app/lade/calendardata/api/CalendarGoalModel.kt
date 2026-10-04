package app.lade.calendardata.api

data class CalendarGoalModel(
    val id: Long,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val actualAmount: Int?,
    val plannedAmount: Int?,
)