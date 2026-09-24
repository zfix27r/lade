package app.lade.draft.internal.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import app.lade.entrykind.EntryKind
import app.lade.ui.theme.ButtonSizes
import app.lade.ui.theme.IconSizes

@Composable
internal fun BarChatKindIcon(
    kind: EntryKind,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon: ImageVector = when (kind) {
        EntryKind.TASK -> Icons.Outlined.CheckCircle
        EntryKind.EVENT -> Icons.Outlined.Event
        EntryKind.HABIT -> Icons.Outlined.Repeat
        EntryKind.SCHEDULE -> Icons.Outlined.CalendarMonth
        EntryKind.UNKNOWN -> Icons.Outlined.HelpOutline
    }

    val tint = when (kind) {
        EntryKind.TASK -> MaterialTheme.colorScheme.primary
        EntryKind.EVENT -> MaterialTheme.colorScheme.tertiary
        EntryKind.HABIT -> MaterialTheme.colorScheme.secondary
        EntryKind.SCHEDULE -> MaterialTheme.colorScheme.error
        EntryKind.UNKNOWN -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .size(ButtonSizes.touchTarget)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(IconSizes.md),
        )
    }
}