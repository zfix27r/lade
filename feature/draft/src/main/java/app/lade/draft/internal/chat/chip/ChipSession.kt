package app.lade.draft.internal.chat.chip

import app.lade.draft.internal.chat.chip.domain.ChipController
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draft.internal.chat.chip.domain.ChipState
import app.lade.draft.internal.chat.chip.domain.ChipStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChipSession @Inject constructor(
    private val controller: ChipController,
) {
    private val _states = MutableStateFlow(ChipStates())
    val states: StateFlow<ChipStates> = _states.asStateFlow()

    val editingKey: ChipKey? get() = _states.value.editingKey

    fun onTap(key: ChipKey): Boolean {
        val before = _states.value.stateOf(key)
        _states.value = controller.onTap(_states.value, key)
        val after = _states.value.stateOf(key)
        return before != ChipState.EDITING && after == ChipState.EDITING
    }

    fun onEditExit(): Boolean {
        val before = _states.value.editingKey
        _states.value = controller.onTap(_states.value, before ?: return false)
        return _states.value.editingKey == null && before != null
    }

    fun onRemove(key: ChipKey) {
        _states.value = controller.onRemove(_states.value, key)
    }

    fun onPropose(keys: Set<ChipKey>) {
        _states.value = controller.onPropose(_states.value, keys)
    }

    fun onCommitAll() {
        _states.value = controller.onCommitAll(_states.value)
    }

    fun onClear() {
        _states.value = controller.onClear(_states.value)
    }
}