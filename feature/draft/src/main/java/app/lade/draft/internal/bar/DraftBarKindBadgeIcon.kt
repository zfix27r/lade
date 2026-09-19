package app.lade.draft.internal.bar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import app.lade.entrykind.EntryKind

@Composable
internal fun DraftBarKindBadgeIcon(
    kind: EntryKind?,
    modifier: Modifier = Modifier,
    size: Dp,
) {
    val icon: ImageVector = when (kind) {
        EntryKind.TASK -> Icons.Default.CheckCircle
        EntryKind.EVENT -> Icons.Default.Event
        EntryKind.HABIT -> Icons.Default.Repeat
        EntryKind.SCHEDULE -> Icons.Default.DateRange
        EntryKind.UNKNOWN, null -> Icons.Default.HelpOutline
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(size),
        )
    }
}