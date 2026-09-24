package app.lade.draft.internal.bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.ui.graphics.vector.ImageVector
import app.lade.entrykind.EntryKind

internal fun EntryKind.icon(): ImageVector = when (this) {
    EntryKind.TASK -> Icons.Default.CheckCircle
    EntryKind.EVENT -> Icons.Default.Event
    EntryKind.HABIT -> Icons.Default.Repeat
    EntryKind.SCHEDULE -> Icons.Default.DateRange
    EntryKind.UNKNOWN -> Icons.AutoMirrored.Filled.HelpOutline
}