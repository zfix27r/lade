package app.lade.draft.internal.ui.bar

import app.lade.draft.internal.chat.chip.ChipKind

internal sealed interface DraftBarEvent {
    data class TextChange(val text: String) : DraftBarEvent
    data object Submit : DraftBarEvent
    data object Cancel : DraftBarEvent
    data object KindClick : DraftBarEvent
    data object Resume : DraftBarEvent
    data object DismissResume : DraftBarEvent
    data class ChipClick(val kind: ChipKind, val index: Int) : DraftBarEvent
    data class ChipRemove(val kind: ChipKind, val index: Int) : DraftBarEvent
    data object ModeSwitch : DraftBarEvent
}