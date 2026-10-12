package app.lade.calendar.internal.card.expandable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendardata.api.agenda.CalendarDataGoalModel

@Composable
internal fun CardExpandableGoalRow(
    goal: CalendarDataGoalModel,
    onBinaryClick: () -> Unit,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val plannedAmount = goal.plannedAmount
    val isMeasurable = plannedAmount != null && plannedAmount > 0
    if (isMeasurable) {
        CardExpandableGoalMeasurableRow(
            goal = goal,
            onValueChange = onValueChange,
            modifier = modifier,
        )
    } else {
        CardExpandableGoalBinaryRow(
            goal = goal,
            onClick = onBinaryClick,
            modifier = modifier,
        )
    }
}