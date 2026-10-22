package app.lade.calendar.internal.list

import app.lade.calendardata.api.models.CalendarDataGoalModel

internal data class ListGoalsVisibility(
    val visible: List<CalendarDataGoalModel>,
    val hiddenCount: Int,
) {
    companion object {
        fun of(
            pending: List<CalendarDataGoalModel>,
            expanded: Boolean,
            limit: Int,
        ): ListGoalsVisibility {
            val visible = if (expanded) pending else pending.take(limit)
            return ListGoalsVisibility(
                visible = visible,
                hiddenCount = pending.size - visible.size,
            )
        }
    }
}