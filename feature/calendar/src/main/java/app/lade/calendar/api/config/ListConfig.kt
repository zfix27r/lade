package app.lade.calendar.api.config

import androidx.compose.ui.unit.Dp
import app.lade.ui.theme.Radius
import app.lade.ui.theme.Spacing
import app.lade.ui.theme.StrokeWidth

data class ListConfig(
    val contentPadding: Dp = Spacing.lg,
    val contentBottomPadding: Dp = Spacing.lg,
    val entrySpacing: Dp = Spacing.sm,
    val entryCornerRadius: Dp = Radius.sm,
    val entryStripeWidth: Dp = StrokeWidth.accent,
    val enableMarkHaptics: Boolean = true,
    val bottomPaddingForInputBar: Dp = Spacing.huge,
    val goalsVisibilityLimit: Int = 3,
)

val DefaultListConfig = ListConfig()