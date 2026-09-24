package app.lade.draft.internal.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs
import app.lade.draft.internal.bar.BarMode
import app.lade.ui.theme.ButtonSizes
import app.lade.ui.theme.IconSizes

private const val SWIPE_THRESHOLD = 40f

@Composable
internal fun BarInputModeIcon(
    mode: BarMode,
    onClick: () -> Unit,
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon: ImageVector = when (mode) {
        BarMode.Chat -> Icons.AutoMirrored.Filled.Chat
        BarMode.Chip -> Icons.Default.GridView
        BarMode.Editor -> Icons.Default.Edit
    }

    Box(
        modifier = modifier
            .size(ButtonSizes.touchTarget)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onDragEnd = {
                        if (abs(totalDrag) > SWIPE_THRESHOLD) {
                            onSwipe()
                        }
                        totalDrag = 0f
                    },
                ) { _, dragAmount ->
                    totalDrag += dragAmount
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(IconSizes.md),
        )
    }
}