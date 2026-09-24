package app.lade.draft.internal.chat

import app.lade.draft.internal.chat.chip.BarChatChip

internal data class DraftChips(
    val field: List<BarChatChip>,
    val goal: List<BarChatChip>,
)