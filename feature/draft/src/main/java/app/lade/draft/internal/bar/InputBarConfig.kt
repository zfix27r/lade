package app.lade.draft.internal.bar

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class InputBarConfig(
    val contentHorizontalPadding: Dp = 16.dp,
    val contentVerticalPadding: Dp = 12.dp,
    val fieldCornerRadius: Dp = 24.dp,
    val fieldMinHeight: Dp = 48.dp,
    val fieldMaxLines: Int = 5,
    val actionIconSize: Dp = 22.dp,
    val actionButtonSize: Dp = 36.dp,
    val kindBadgeSize: Dp = 22.dp,
)

internal val DefaultInputBarConfig = InputBarConfig()