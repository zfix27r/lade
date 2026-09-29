package app.lade.draft.internal.chat.chip

import app.lade.draft.internal.chat.chip.data.ChipKey

internal interface ChipOutPort {
    val onEdit: (ChipKey) -> Unit
    val onEditExit: () -> Unit
    val onRemove: (ChipKey) -> Unit
    val onPendingChange: (Boolean) -> Unit
}