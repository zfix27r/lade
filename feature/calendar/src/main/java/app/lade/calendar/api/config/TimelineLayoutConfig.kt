package app.lade.calendar.api.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.ui.theme.IconSizes
import app.lade.ui.theme.Radius
import app.lade.ui.theme.Spacing
import app.lade.ui.theme.StrokeWidth

data class TimelineLayoutConfig(
    val railWidth: Dp = 64.dp,
    val railPadding: Dp = Spacing.md,
    val dayPadding: Dp = Spacing.md,
    val daySpacing: Dp = Spacing.sm,
    val cardCornerRadius: Dp = Radius.sm,
    val cardStripeWidth: Dp = StrokeWidth.accent,
    val contentPadding: Dp = Spacing.lg,
    val contentBottomPadding: Dp = Spacing.lg,
    val bottomPaddingForInputBar: Dp = Spacing.huge,
    val showEmptyDays: Boolean = true,
    val enableMarkHaptics: Boolean = true,
    val emptyDayHeight: Dp = Spacing.xxl,
    val dayCircleSize: Dp = IconSizes.lg,
    val dayNumberPadding: Dp = Spacing.xs,
)

val DefaultTimelineLayoutConfig = TimelineLayoutConfig()