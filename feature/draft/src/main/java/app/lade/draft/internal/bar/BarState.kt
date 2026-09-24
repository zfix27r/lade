package app.lade.draft.internal.bar

import androidx.compose.runtime.Stable
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal data class BarState(
    val isFocused: Boolean = false,
    val mode: BarMode = BarMode.Chat,
    val rawInput: String = "",
    val resetGeneration: Int = 0,
    val focusRequestGeneration: Int = 0,
)

@Stable
internal class BarStateHolder {

    private val _state = MutableStateFlow(BarState())
    val state: StateFlow<BarState> = _state.asStateFlow()

    fun onFocusChange(focusState: FocusState) {
        _state.update { it.copy(isFocused = focusState.isFocused) }
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

    fun requestFocus() {
        _state.update { it.copy(focusRequestGeneration = it.focusRequestGeneration + 1) }
    }

    fun clearFocus() {
        _state.update {
            it.copy(
                isFocused = false,
                resetGeneration = it.resetGeneration + 1,
            )
        }
    }

    fun clearInput() {
        _state.update { it.copy(rawInput = "") }
    }
}

internal fun Modifier.barFocus(onFocusChange: (FocusState) -> Unit): Modifier =
    onFocusChanged(onFocusChange)