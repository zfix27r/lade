package app.lade.calendar.api.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.ui.theme.Radius
import app.lade.ui.theme.Spacing
import app.lade.ui.theme.StrokeWidth

data class WeekConfig(
    val horizontalPadding: Dp = Spacing.lg,
    val verticalPadding: Dp = Spacing.sm,
    val cellSpacing: Dp = Spacing.xs,
    val cellCornerRadius: Dp = Radius.xs,
    val cellBorderWidth: Dp = StrokeWidth.hairline,
    val cellVerticalPadding: Dp = Spacing.sm,
    val dotSize: Dp = Spacing.xs,
    val dotPadding: Dp = Spacing.xs,
    val gridPadding: Dp = Spacing.sm,
    val hourHeight: Dp = Spacing.xxxl,
    val hourLabelWidth: Dp = 40.dp,
    val dayColumnWidth: Dp = 72.dp,
    val gridCellBorderWidth: Dp = StrokeWidth.hairline,
    val gridCellPadding: Dp = StrokeWidth.hairline,
)

val DefaultWeekConfig = WeekConfig()