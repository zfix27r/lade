package app.lade.entry.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.lade.entry.EntryKind

@Composable
fun EntryKind.color(): Color = when (this) {
	EntryKind.NOTE -> MaterialTheme.colorScheme.onTertiaryFixed
	EntryKind.TASK -> MaterialTheme.colorScheme.primary
	EntryKind.EVENT -> MaterialTheme.colorScheme.tertiary
	EntryKind.HABIT -> MaterialTheme.colorScheme.secondary
	EntryKind.SCHEDULE -> MaterialTheme.colorScheme.error
}