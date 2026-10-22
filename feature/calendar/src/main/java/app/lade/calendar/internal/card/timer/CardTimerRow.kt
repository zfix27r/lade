package app.lade.calendar.internal.card.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.lade.calendardata.api.models.CalendarDataTimerModel
import kotlinx.coroutines.delay

@Composable
internal fun CardTimerRow(
    timer: CalendarDataTimerModel,
    modifier: Modifier = Modifier,
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val startedAtMs = timer.startedAtMs
    val actualMinutes = timer.actualMinutes

    LaunchedEffect(startedAtMs, actualMinutes) {
        if (startedAtMs != null && actualMinutes == null) {
            while (true) {
                delay(60_000)
                now = System.currentTimeMillis()
            }
        }
    }

    val durationMinutes = timer.durationMinutes
    val elapsed = when {
        actualMinutes != null -> actualMinutes
        startedAtMs != null -> ((now - startedAtMs).coerceAtLeast(0L) / 60_000L).toInt()
        else -> 0
    }

    val fraction = if (durationMinutes > 0) {
        (elapsed.toFloat() / durationMinutes).coerceIn(0f, 1f)
    } else 0f

    val isOverrun = elapsed > durationMinutes
    val isComplete = elapsed >= durationMinutes

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Таймер",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${formatTimerRowTime(elapsed)} / ${formatTimerRowTime(durationMinutes)}",
                style = MaterialTheme.typography.labelMedium,
                color = when {
                    isOverrun -> MaterialTheme.colorScheme.error
                    isComplete -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }

        LinearProgressIndicator(
            progress = { fraction },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
        )
    }
}

private fun formatTimerRowTime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return "%02d:%02d".format(h, m)
}