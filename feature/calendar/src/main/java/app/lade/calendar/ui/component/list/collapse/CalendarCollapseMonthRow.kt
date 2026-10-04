package app.lade.calendar.ui.component.list.collapse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun CalendarCollapseMonthRow(
    week: List<LocalDate?>,
    currentDate: LocalDate,
    datesWithEntries: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    rowAlpha: Float = 1f,
    horizontalPadding: Dp = 0.dp,
) {
    val today = LocalDate.now()
    Row(
        modifier = modifier
            .alpha(rowAlpha)
            .padding(horizontal = horizontalPadding),
    ) {
        week.forEach { date ->
            val cellModifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .then(
                    if (date != null) Modifier.clickable { onDateSelected(date) }
                    else Modifier,
                )
            Box(
                modifier = cellModifier,
                contentAlignment = Alignment.Center,
            ) {
                if (date != null) {
                    DayCircle(
                        date = date,
                        isActive = date == currentDate,
                        isToday = date == today,
                        hasEntries = date in datesWithEntries,
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCircle(
    date: LocalDate,
    isActive: Boolean,
    isToday: Boolean,
    hasEntries: Boolean,
) {
    val borderColor = when {
        isActive -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.tertiary
        else -> Color.Transparent
    }
    val containerColor = when {
        isActive -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val textColor = when {
        isActive -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.tertiary
        hasEntries -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(containerColor)
            .then(
                if (!isActive && (isToday || hasEntries)) {
                    Modifier.border(1.dp, borderColor, CircleShape)
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = textColor,
        )
    }
}