package app.lade.calendar.internal.list.card.expandable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lade.calendardata.api.CalendarGoalExpandedModel

@Composable
internal fun CardExpandableDetails(
    details: List<CalendarGoalExpandedModel>?,
    onBinaryClick: (Long) -> Unit,
    onValueChange: (Long, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (details == null) return
    if (details.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        details.forEach { goal ->
            CardExpandableGoalRow(
                goal = goal,
                onBinaryClick = { onBinaryClick(goal.id) },
                onValueChange = { value -> onValueChange(goal.id, value) },
            )
        }
    }
}