package app.lade.calendar.internal.list.card.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lade.calendar.api.config.TimerConfig
import app.lade.calendardata.api.CalendarCardModel
import app.lade.calendardata.api.CalendarTimerModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun CardTimer(
    card: CalendarCardModel,
    timer: CalendarTimerModel,
    containerColor: Color,
    timerConfig: TimerConfig,
    onStart: () -> Unit,
    onFinish: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val startedAtMs = timer.startedAtMs
    val actualMinutes = timer.actualMinutes
    val durationMinutes = timer.durationMinutes

    LaunchedEffect(startedAtMs, actualMinutes) {
        if (startedAtMs != null && actualMinutes == null) {
            while (true) {
                delay(60_000.milliseconds)
                now = System.currentTimeMillis()
            }
        }
    }

    val rawElapsed = when {
        actualMinutes != null -> actualMinutes
        startedAtMs != null ->
            ((now - startedAtMs).coerceAtLeast(0L) / 60_000L).toInt()

        else -> 0
    }

    val isRunning = startedAtMs != null && actualMinutes == null
    val isFinished = actualMinutes != null

    val staleThreshold = (durationMinutes * timerConfig.resetMultiplier).toInt()
    val isStale = isRunning && rawElapsed > staleThreshold

    val effectiveRunning = isRunning && !isStale
    val elapsedMinutes = if (isStale) 0 else rawElapsed

    val isOverrun = isFinished && elapsedMinutes > durationMinutes
    val isComplete = isFinished && elapsedMinutes >= durationMinutes

    val numbersText = if (effectiveRunning || isFinished) {
        "${formatTimerTime(elapsedMinutes)} / ${formatTimerTime(durationMinutes)}"
    } else {
        formatTimerTime(durationMinutes)
    }

    val numbersColor = when {
        isOverrun -> MaterialTheme.colorScheme.error
        isComplete -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = card.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        if (card.timeFrom != null) {
            Text(
                text = card.timeFrom.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = numbersText,
            style = MaterialTheme.typography.labelMedium,
            color = numbersColor,
        )

        when {
            !effectiveRunning && !isFinished -> {
                IconButton(onClick = onStart) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Старт",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            effectiveRunning -> {
                IconButton(onClick = {
                    val elapsed = ((System.currentTimeMillis() - startedAtMs) / 60_000L).toInt()
                    onFinish(elapsed)
                }) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Завершить",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            true -> {
                IconButton(onClick = onStart) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Новый",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun formatTimerTime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return "%02d:%02d".format(h, m)
}