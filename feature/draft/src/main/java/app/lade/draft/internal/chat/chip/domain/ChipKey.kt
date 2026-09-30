package app.lade.draft.internal.chat.chip.domain

import app.lade.draft.internal.chat.chip.ChipKind

internal data class ChipKey(
    val kind: ChipKind,
    val index: Int,
)