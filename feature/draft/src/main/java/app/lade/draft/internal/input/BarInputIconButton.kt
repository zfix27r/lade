package app.lade.draft.internal.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import app.lade.ui.theme.ButtonSizes
import app.lade.ui.theme.IconSizes

@Composable
internal fun BarInputIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = LocalContentColor.current,
    buttonSize: Dp = ButtonSizes.md,
    iconSize: Dp = IconSizes.md,
    touchTarget: Dp = ButtonSizes.touchTarget,
) {
    val actualTint = if (enabled) tint else tint.copy(alpha = 0.38f)

    Box(
        modifier = modifier
            .size(touchTarget)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = actualTint,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}