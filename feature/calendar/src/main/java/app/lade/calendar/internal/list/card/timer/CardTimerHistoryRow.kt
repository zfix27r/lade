package app.lade.calendar.internal.list.card.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.lade.calendardata.api.CalendarTimerHistoryItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun CardTimerHistoryRow(
    item: CalendarTimerHistoryItem,
    modifier: Modifier = Modifier,
) {
    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }
    val startedText = remember(item.startedAtMs) {
        Instant.ofEpochMilli(item.startedAtMs)
            .atZone(ZoneId.systemDefault())
            .format(timeFormatter)
    }

    val isFinished = item.actualMinutes != null
    val isOverrun = isFinished && item.actualMinutes!! > item.plannedMinutes
    val isComplete = isFinished && item.actualMinutes!! >= item.plannedMinutes

    val numbersText = if (isFinished) {
        "${formatTime(item.actualMinutes!!)} / ${formatTime(item.plannedMinutes)}"
    } else {
        "идёт · ${formatTime(item.plannedMinutes)}"
    }

    val numbersColor = when {
        isOverrun -> MaterialTheme.colorScheme.error
        isComplete -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = startedText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = numbersText,
            style = MaterialTheme.typography.labelMedium,
            color = numbersColor,
            modifier = Modifier.weight(1f),
        )
        if (!isFinished) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}