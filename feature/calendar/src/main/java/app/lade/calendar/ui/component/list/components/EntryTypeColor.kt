package app.lade.calendar.ui.component.list.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.lade.entrykind.EntryKind

@Composable
fun entryTypeColor(kind: EntryKind): Color = when (kind) {
    EntryKind.HABIT -> MaterialTheme.colorScheme.primary
    EntryKind.TASK -> MaterialTheme.colorScheme.tertiary
    EntryKind.EVENT -> MaterialTheme.colorScheme.secondary
    EntryKind.SCHEDULE -> MaterialTheme.colorScheme.onSurfaceVariant
    EntryKind.UNKNOWN -> MaterialTheme.colorScheme.outline
}