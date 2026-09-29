package app.lade.draft.internal.chiper

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.entry.EntryKind
import app.lade.entry.ui.icon
import app.lade.ui.theme.IconSizes

@Composable
internal fun BarChipKindIcon(
    entryKind: EntryKind,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = entryKind.icon(),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(IconSizes.md),
    )
}