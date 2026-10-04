package app.lade.draft.internal.chat.chip.domain

import androidx.compose.ui.graphics.vector.ImageVector

internal data class ChipData(
    val key: ChipKey,
    val icon: ImageVector,
    val value: String,
)