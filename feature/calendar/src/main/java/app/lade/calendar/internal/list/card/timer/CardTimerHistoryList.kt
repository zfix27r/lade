package app.lade.calendar.internal.list.card.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lade.calendardata.api.CalendarTimerHistoryItem


@Composable
internal fun CardTimerHistoryList(
    history: List<CalendarTimerHistoryItem>,
    modifier: Modifier = Modifier,
) {
    if (history.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "История",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        history.forEach { item ->
            CardTimerHistoryRow(item = item)
        }
    }
}