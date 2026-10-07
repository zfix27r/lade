package app.lade.calendar.internal.list.strip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import app.lade.calendar.api.config.StripConfig
import app.lade.calendardata.api.DayProgress
import java.time.LocalDate

@Composable
internal fun StripMonthDayCircle(
    date: LocalDate,
    isActive: Boolean,
    isToday: Boolean,
    progress: DayProgress?,
    isOutOfMonth: Boolean,
    config: StripConfig,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val arcColor = when {
        isActive -> onPrimary
        isOutOfMonth -> onSurfaceVariant.copy(alpha = config.outOfMonthAlpha)
        else -> primary
    }
    val textColor = when {
        isActive -> onPrimary
        isToday -> tertiary
        isOutOfMonth -> onSurfaceVariant.copy(alpha = config.outOfMonthAlpha)
        progress != null -> onSurface
        else -> onSurfaceVariant
    }

    Box(
        modifier = Modifier.size(config.circleSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (isActive) {
                drawCircle(color = primary)
            }
            if (progress != null) {
                val stroke = config.arcWidth.toPx()
                val diameter = size.minDimension - stroke
                val baseHalf = config.arcBaseSweep / 2f
                val extra = if (progress.total == 0) 0f else 180f * progress.fraction
                val half = (baseHalf + extra).coerceAtMost(180f)

                drawArc(
                    color = arcColor,
                    startAngle = 90f - half,
                    sweepAngle = half,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(diameter, diameter),
                )
                drawArc(
                    color = arcColor,
                    startAngle = 90f,
                    sweepAngle = half,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(diameter, diameter),
                )
            }
        }
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = textColor,
        )
    }
}