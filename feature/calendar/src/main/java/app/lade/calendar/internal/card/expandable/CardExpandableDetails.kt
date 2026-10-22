package app.lade.calendar.internal.card.expandable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lade.calendardata.api.models.CalendarDataGoalModel

@Composable
internal fun CardExpandableDetails(
    goals: List<CalendarDataGoalModel>,
    onBinaryClick: (Long) -> Unit,
    onValueChange: (Long, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (goals.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        goals.forEach { goal ->
            CardExpandableGoalRow(
                goal = goal,
                onBinaryClick = { onBinaryClick(goal.id) },
                onValueChange = { value -> onValueChange(goal.id, value) },
            )
        }
    }
}