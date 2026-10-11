package app.lade.calendardata.api

import app.lade.entry.EntryKind
import java.time.LocalDate
import java.time.LocalTime

data class CalendarCardModel(
    val date: LocalDate,
    val entryId: Long,
    val entryKind: EntryKind,
    val title: String,
    val timeFrom: LocalTime?,
    val timeTo: LocalTime?,
    val isSeries: Boolean,
    val isAlarm: Boolean,
    val isVisited: Boolean,
    val goals: List<CalendarGoalModel>,
    val goalsDone: Int,
    val goalsTotal: Int,
    val daysLeft: Int? = null,
    val timer: CalendarTimerModel? = null,
) {
    val hasGoals: Boolean get() = goalsTotal > 0
    val allGoalsDone: Boolean get() = hasGoals && goalsDone == goalsTotal
    val someGoalsDone: Boolean get() = goalsDone in 1 until goalsTotal
    val noGoalsDone: Boolean get() = goalsDone == 0
    val pendingGoals: List<CalendarGoalModel> get() = goals.filter { !it.isDone }
}