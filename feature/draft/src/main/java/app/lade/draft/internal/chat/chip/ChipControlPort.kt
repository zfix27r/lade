package app.lade.draft.internal.chat.chip

import app.lade.draft.internal.chat.chip.domain.ChipData
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draft.internal.chat.chip.domain.ChipStates
import kotlinx.coroutines.flow.StateFlow

internal interface ChipControlPort {
    val chips: StateFlow<List<ChipData>>
    val states: StateFlow<ChipStates>

    fun onClickEdit(key: ChipKey)
    fun onClickRemove(key: ChipKey)
}