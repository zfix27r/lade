package app.lade.calendar.internal.dates

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
internal fun buildCalendarDatesStyle(
    compact: Boolean,
    visibleWeeks: Int,
): CalendarDatesStyle {
    val cs = MaterialTheme.colorScheme
    return if (compact) {
        CalendarDatesStyle(
            dayCircleSize = 28.dp,
            markerDotSize = 4.dp,
            markerSpacing = 2.dp,
            cellPadding = 2.dp,
            visibleWeeks = visibleWeeks,
            dayTextStyle = MaterialTheme.typography.labelSmall,
            selectedFill = cs.primary,
            selectedText = cs.onPrimary,
            todayFill = cs.primaryContainer,
            todayText = cs.onPrimaryContainer,
            markerColor = cs.primary,
            weekdayTextStyle = MaterialTheme.typography.labelSmall,
            weekdayTextColor = cs.onSurfaceVariant,
        )
    } else {
        CalendarDatesStyle(
            dayCircleSize = 36.dp,
            markerDotSize = 6.dp,
            markerSpacing = 4.dp,
            cellPadding = 4.dp,
            visibleWeeks = visibleWeeks,
            dayTextStyle = MaterialTheme.typography.bodyMedium,
            selectedFill = cs.primary,
            selectedText = cs.onPrimary,
            todayFill = cs.primaryContainer,
            todayText = cs.onPrimaryContainer,
            markerColor = cs.primary,
            weekdayTextStyle = MaterialTheme.typography.labelMedium,
            weekdayTextColor = cs.onSurfaceVariant,
        )
    }
}