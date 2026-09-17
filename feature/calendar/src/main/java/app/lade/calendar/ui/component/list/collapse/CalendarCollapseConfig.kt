package app.lade.calendar.ui.component.list.collapse

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.ui.gesture.SnapToEdgeConfig

data class CalendarCollapseConfig(
    val rowHeight: Dp = 40.dp,
    val rowHorizontalPadding: Dp = 0.dp,
    val inactiveRowMinAlpha: Float = 0.3f,
    val fullScrollRows: Int = 5,
    val snapToEdge: SnapToEdgeConfig = SnapToEdgeConfig(),
)

val DefaultCalendarCollapseConfig = CalendarCollapseConfig()