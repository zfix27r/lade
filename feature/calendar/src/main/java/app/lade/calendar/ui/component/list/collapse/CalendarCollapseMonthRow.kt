package app.lade.calendar.ui.component.list.collapse

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.calendardata.api.DayProgress
import java.time.LocalDate

@Composable
fun CalendarCollapseMonthRow(
    week: List<LocalDate>,
    currentDate: LocalDate,
    pageDate: LocalDate,
    markedDates: Map<LocalDate, DayProgress>,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 0.dp,
) {
    val today = LocalDate.now()
    val pageMonth = pageDate.month
    Row(
        modifier = modifier.padding(horizontal = horizontalPadding),
    ) {
        week.forEach { date ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                DayCircle(
                    date = date,
                    isActive = date == currentDate,
                    isToday = date == today,
                    progress = markedDates[date],
                    isOutOfMonth = date.month != pageMonth,
                )
            }
        }
    }
}

private const val BASE_SWEEP = 8f

@Composable
private fun DayCircle(
    date: LocalDate,
    isActive: Boolean,
    isToday: Boolean,
    progress: DayProgress?,
    isOutOfMonth: Boolean,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val arcColor = when {
        isActive -> onPrimary
        isOutOfMonth -> onSurfaceVariant.copy(alpha = 0.35f)
        else -> primary
    }
    val textColor = when {
        isActive -> onPrimary
        isToday -> tertiary
        isOutOfMonth -> onSurfaceVariant.copy(alpha = 0.35f)
        progress != null -> onSurface
        else -> onSurfaceVariant
    }

    Box(
        modifier = Modifier.size(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (isActive) {
                drawCircle(color = primary)
            }
            if (progress != null) {
                val stroke = 1.dp.toPx()
                val diameter = size.minDimension - stroke
                val baseHalf = BASE_SWEEP / 2f
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