package app.lade.draft.internal.chat.chip

import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draft.internal.chat.chip.domain.ChipStates
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal interface ChipOutsidePort {
    val states: StateFlow<ChipStates>
    val clickEdit: SharedFlow<ChipKey>
    val clickRemove: SharedFlow<ChipKey>

    fun onPropose(keys: Set<ChipKey>)
    fun onCommitAll()
    fun onClear()
}