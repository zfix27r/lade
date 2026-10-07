package app.lade.calendar.internal.list.data

import app.lade.calendardata.api.CalendarGoalModel

internal data class ListGoalsVisibility(
    val visible: List<CalendarGoalModel>,
    val hiddenCount: Int,
) {
    companion object {
        fun of(
            pending: List<CalendarGoalModel>,
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