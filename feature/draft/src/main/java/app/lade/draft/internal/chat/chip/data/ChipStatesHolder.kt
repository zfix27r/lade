package app.lade.draft.internal.chat.chip.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ChipStatesHolder(
    private val controller: ChipController,
) {
    private val _chipStates = MutableStateFlow(ChipStates())
    val chipStates: StateFlow<ChipStates> = _chipStates.asStateFlow()

    fun onTap(key: ChipKey) {
        _chipStates.value = controller.onTap(_chipStates.value, key)
    }

    fun onRemove(key: ChipKey) {
        _chipStates.value = controller.onRemove(_chipStates.value, key)
    }

    fun onPropose(keys: Set<ChipKey>) {
        _chipStates.value = controller.onPropose(_chipStates.value, keys)
    }

    fun onCommitAll() {
        _chipStates.value = controller.onCommitAll(_chipStates.value)
    }

    fun onClear() {
        _chipStates.value = controller.onClear(_chipStates.value)
    }
}