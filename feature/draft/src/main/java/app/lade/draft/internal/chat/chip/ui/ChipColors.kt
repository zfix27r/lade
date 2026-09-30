package app.lade.draft.internal.chat.chip.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import app.lade.draft.internal.chat.chip.domain.ChipEmphasis

@Composable
internal fun chipColors(emphasis: ChipEmphasis): ChipColorsModel {
    val scheme = MaterialTheme.colorScheme
    return if (emphasis == ChipEmphasis.ACTIVE) {
        ChipColorsModel(
            container = scheme.secondaryContainer,
            content = scheme.onSecondaryContainer,
            border = scheme.outlineVariant,
        )
    } else {
        ChipColorsModel(
            container = scheme.surfaceContainerHighest.copy(alpha = 0.5f),
            content = scheme.onSurfaceVariant.copy(alpha = 0.6f),
            border = scheme.outlineVariant.copy(alpha = 0.4f),
        )
    }
}