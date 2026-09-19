package app.lade.calendar.ui.component.list

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseConfig
import app.lade.calendar.ui.component.list.collapse.DefaultCalendarCollapseConfig

data class CalendarListConfig(
    val collapse: CalendarCollapseConfig = DefaultCalendarCollapseConfig,
    val listTopPaddingWhenCollapsed: Dp = 0.dp,
    val listContentHorizontalPadding: Dp = 16.dp,
    val listContentBottomPadding: Dp = 16.dp,
    val listEntrySpacing: Dp = 8.dp,
    val listEntryCornerRadius: Dp = 12.dp,
    val listEntryTypeStripeWidth: Dp = 3.dp,
    val showEmptyState: Boolean = true,
    val enableMarkHaptics: Boolean = true,
    val enableEntryAnimations: Boolean = true,
    val listBottomPaddingForInputBar: Dp = 80.dp
)

val DefaultCalendarListConfig = CalendarListConfig()