package app.lade.entry.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Note
import androidx.compose.ui.graphics.vector.ImageVector
import app.lade.entry.EntryKind

fun EntryKind.icon(): ImageVector = when (this) {
	EntryKind.NOTE -> Icons.Outlined.Note
	EntryKind.TASK -> Icons.Outlined.CheckCircle
	EntryKind.EVENT -> Icons.Outlined.Event
	EntryKind.HABIT -> Icons.Outlined.Autorenew
	EntryKind.SCHEDULE -> Icons.Outlined.CalendarMonth
}