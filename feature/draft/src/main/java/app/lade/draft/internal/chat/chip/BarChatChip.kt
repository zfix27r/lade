package app.lade.draft.internal.chat.chip

import androidx.compose.ui.graphics.vector.ImageVector

internal data class BarChatChip(
    val icon: ImageVector,
    val value: String,
    val kind: BarChipKind,
    val index: Int = 0,
    val onClick: () -> Unit,
    val onRemove: (() -> Unit)? = null,
)