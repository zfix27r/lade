package app.lade.calendardata.api.models

data class CalendarDataGoalsModel(
    val goals: List<CalendarDataGoalModel>,
    val done: Int,
    val total: Int,
) {
    val isAllDone: Boolean = total > 0 && done == total
}