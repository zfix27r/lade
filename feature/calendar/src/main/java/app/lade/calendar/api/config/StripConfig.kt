package app.lade.calendar.api.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.ui.gesture.SnapToEdgeConfig
import app.lade.ui.theme.IconSizes
import app.lade.ui.theme.Spacing
import app.lade.ui.theme.StrokeWidth

data class StripConfig(
    val rowHeight: Dp = 40.dp,
    val rowPadding: Dp = Spacing.none,
    val scrollRows: Int = 5,
    val snapToEdge: SnapToEdgeConfig = SnapToEdgeConfig(),
    val shiftThreshold: Float = 0.5f,
    val releaseThreshold: Float = 0.20f,
    val swipeFlingVelocityThreshold: Float = 1000f,
    val swipeVerticalReleaseThreshold: Float = 0.5f,
    val swipeVerticalFlingVelocityThreshold: Float = 1000f,
    val pageRange: Int = 2,
    val circleSize: Dp = IconSizes.xl,
    val arcWidth: Dp = StrokeWidth.hairline,
    val arcBaseSweep: Float = 8f,
    val outOfMonthAlpha: Float = 0.35f,
    val outOfMonthAlphaStart: Float = 0.7f,
    val outOfMonthAlphaThreshold: Float = 0.2f,
)

val DefaultStripConfig = StripConfig()