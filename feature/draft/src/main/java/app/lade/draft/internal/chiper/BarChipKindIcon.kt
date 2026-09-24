package app.lade.draft.internal.chiper

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.draft.internal.bar.icon
import app.lade.entrykind.EntryKind
import app.lade.ui.theme.IconSizes

@Composable
internal fun BarChipKindIcon(
    kind: EntryKind,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = kind.icon(),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(IconSizes.md),
    )
}