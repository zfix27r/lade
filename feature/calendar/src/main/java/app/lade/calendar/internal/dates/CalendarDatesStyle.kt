package app.lade.calendar.internal.dates

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@Immutable
internal data class CalendarDatesStyle(
    val dayCircleSize: Dp,
    val markerDotSize: Dp,
    val markerSpacing: Dp,
    val cellPadding: Dp,
    val visibleWeeks: Int,
    val dayTextStyle: TextStyle,
    val selectedFill: Color,
    val selectedText: Color,
    val todayFill: Color,
    val todayText: Color,
    val markerColor: Color,
    val weekdayTextStyle: TextStyle,
    val weekdayTextColor: Color,
)