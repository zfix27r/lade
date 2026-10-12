package app.lade.calendar.internal.card.timer

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.lade.calendardata.api.agenda.CalendarDataTimerModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun CardTimerIndicator(
    timer: CalendarDataTimerModel,
    modifier: Modifier = Modifier,
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val startedAtMs = timer.startedAtMs
    val actualMinutes = timer.actualMinutes

    LaunchedEffect(startedAtMs, actualMinutes) {
        if (startedAtMs != null && actualMinutes == null) {
            while (true) {
                delay(60_000.milliseconds)
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

    val isFinished = actualMinutes != null
    val isRunning = startedAtMs != null && !isFinished

    val text = if (isFinished || isRunning) {
        "${formatTimerIndicatorTime(elapsed)} / ${formatTimerIndicatorTime(durationMinutes)}"
    } else {
        formatTimerIndicatorTime(durationMinutes)
    }

    val color = when {
        isFinished && elapsed > durationMinutes -> MaterialTheme.colorScheme.error
        isFinished && elapsed >= durationMinutes -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier,
    )
}

private fun formatTimerIndicatorTime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return "%02d:%02d".format(h, m)
}