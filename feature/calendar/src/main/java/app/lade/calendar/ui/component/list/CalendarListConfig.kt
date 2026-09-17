package app.lade.calendar.ui.component.list

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseConfig
import app.lade.calendar.ui.component.list.collapse.DefaultCalendarCollapseConfig

data class CalendarListConfig(
    val collapse: CalendarCollapseConfig = DefaultCalendarCollapseConfig,
    val listTopPaddingWhenCollapsed: Dp = 0.dp,
)

val DefaultCalendarListConfig = CalendarListConfig()