package app.lade.draft.internal.chat.parse

import app.lade.draft.internal.chat.chip.domain.ChipKey

internal sealed interface InputClearReason {
    data object Save : InputClearReason
    data object Reset : InputClearReason
    data object EditExit : InputClearReason
    data class RemoveEditing(val key: ChipKey) : InputClearReason
}