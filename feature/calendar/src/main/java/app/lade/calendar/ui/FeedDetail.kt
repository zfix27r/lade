package app.lade.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.agenda.api.goal.GoalUnit
import app.lade.calendar.domain.FeedItem
import app.lade.calendar.domain.FeedSource
import app.lade.resources.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun feedDetail(item: FeedItem, timeFmt: DateTimeFormatter): String? {
    val start = item.startMinutes
    val end = item.endMinutes
    if (start != null && end != null) {
        return stringResource(
            R.string.format_time_range,
            LocalTime.ofSecondOfDay(start * 60L).format(timeFmt),
            LocalTime.ofSecondOfDay(end * 60L).format(timeFmt),
        )
    }
    if (item.source == FeedSource.HABIT) {
        val status = if (item.habitDone) {
            stringResource(R.string.habit_status_done)
        } else {
            null
        }
        val unitGoal = item.habitGoalUnit
        val unit = if (unitGoal != null) GoalUnit.fromStorage(unitGoal) else null
        val goal = if (item.habitGoalValue != null && unit != null) {
            stringResource(R.string.format_habit_goal, item.habitGoalValue, unit.storage)
        } else {
            null
        }
        val time = item.habitTimeMinutes?.let { minutes ->
            LocalTime.ofSecondOfDay(minutes * 60L).format(timeFmt)
        }
        return listOfNotNull(status, goal, time).joinToString(" · ").ifBlank { null }
    }
    return null
}