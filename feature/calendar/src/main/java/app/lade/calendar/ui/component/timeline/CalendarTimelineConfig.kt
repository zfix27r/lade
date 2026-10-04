package app.lade.calendar.ui.component.timeline

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class CalendarTimelineConfig(
    val railWidth: Dp = 64.dp,
    val railHorizontalPadding: Dp = 12.dp,
    val dayVerticalPadding: Dp = 12.dp,
    val daySpacing: Dp = 8.dp,
    val cardCornerRadius: Dp = 12.dp,
    val cardStripeWidth: Dp = 3.dp,
    val contentHorizontalPadding: Dp = 16.dp,
    val contentBottomPadding: Dp = 16.dp,
    val bottomPaddingForInputBar: Dp = 80.dp,
    val showEmptyDays: Boolean = true,
    val enableMarkHaptics: Boolean = true,
    val enableEntryAnimations: Boolean = true,
    val loadThresholdDays: Long = 10L,
)

val DefaultCalendarTimelineConfig = CalendarTimelineConfig()