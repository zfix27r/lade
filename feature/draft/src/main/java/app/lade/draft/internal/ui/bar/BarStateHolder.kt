package app.lade.draft.internal.ui.bar

import androidx.compose.runtime.Stable
import androidx.compose.ui.focus.FocusState
import app.lade.draft.api.DraftPhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
internal class BarStateHolder {

    private val _phase = MutableStateFlow(DraftPhase.IDLE)
    val phase: StateFlow<DraftPhase> = _phase.asStateFlow()

    private val _state = MutableStateFlow(BarState())
    val state: StateFlow<BarState> = _state.asStateFlow()

    fun enterEdit() {
        if (_phase.value == DraftPhase.EDIT) return
        _phase.value = DraftPhase.EDIT
        _state.update {
            it.copy(
                focusRequestGeneration = it.focusRequestGeneration + 1,
                keyboardRequestGeneration = it.keyboardRequestGeneration + 1,
            )
        }
    }

    fun enterIdle() {
        if (_phase.value == DraftPhase.IDLE) return
        _phase.value = DraftPhase.IDLE
        _state.update {
            it.copy(
                rawInput = "",
                resetGeneration = it.resetGeneration + 1,
            )
        }
    }

    fun updateRawInput(value: String) {
        _state.update { it.copy(rawInput = value) }
    }

    fun selectMode(newMode: BarMode) {
        _state.update { it.copy(mode = newMode) }
    }

    fun nextMode() {
        _state.update {
            val next = when (it.mode) {
                BarMode.Chat -> BarMode.Chip
                BarMode.Chip -> BarMode.Editor
                BarMode.Editor -> BarMode.Chat
            }
            it.copy(mode = next)
        }
    }

    fun clearInput() {
        _state.update { it.copy(rawInput = "") }
    }

    fun requestFocus() {
        _state.update { it.copy(focusRequestGeneration = it.focusRequestGeneration + 1) }
    }

    fun requestKeyboard() {
        _state.update { it.copy(keyboardRequestGeneration = it.keyboardRequestGeneration + 1) }
    }

    fun onFocusChange(focusState: FocusState) {
        if (focusState.isFocused) {
            enterEdit()
        }
    }
}