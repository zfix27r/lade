package app.lade.draft.internal.chat.chip.domain

import androidx.compose.ui.graphics.vector.ImageVector
import app.lade.draft.internal.chat.chip.ChipKind

internal data class ChipModel(
    val icon: ImageVector,
    val value: String,
    val kind: ChipKind,
    val index: Int = 0,
    val state: ChipState = ChipState.COMMITTED,
    val emphasis: ChipEmphasis = ChipEmphasis.ACTIVE,
    val onClick: () -> Unit,
    val onRemove: (() -> Unit)? = null,
)