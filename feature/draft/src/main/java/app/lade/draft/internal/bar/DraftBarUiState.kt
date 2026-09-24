package app.lade.draft.internal.bar

import app.lade.draft.internal.DraftBarLifecycleState
import app.lade.draft.internal.chat.chip.BarChatChip
import app.lade.draftdata.DraftModel

internal data class DraftBarUiState(
    val isFocused: Boolean = false,
    val mode: BarMode = BarMode.Chat,
    val rawInput: String = "",
    val draft: DraftModel = DraftModel(),
    val fieldChips: List<BarChatChip> = emptyList(),
    val goalChips: List<BarChatChip> = emptyList(),
    val lifecycle: DraftBarLifecycleState = DraftBarLifecycleState(),
)