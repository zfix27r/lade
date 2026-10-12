package app.lade.calendardata.api.agenda

data class CalendarDataGoalsModel(
    val goals: List<CalendarDataGoalModel>,
    val goalsDone: Int,
    val goalsTotal: Int,
    val isAllDone: Boolean,
)