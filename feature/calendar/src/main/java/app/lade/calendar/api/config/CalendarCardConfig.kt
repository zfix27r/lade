package app.lade.calendar.api.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import app.lade.ui.theme.Radius
import app.lade.ui.theme.Spacing
import app.lade.ui.theme.StrokeWidth

data class CalendarCardConfig(
    val cornerRadius: Dp = Radius.sm,
    val stripeWidth: Dp = StrokeWidth.medium,
    val contentPadding: PaddingValues = PaddingValues(
        horizontal = Spacing.lg,
        vertical = Spacing.md,
    ),
    val entrySpacing: Dp = Spacing.sm,
)

val DefaultCalendarCardConfig = CalendarCardConfig()