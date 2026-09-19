package app.lade.draft.internal.bar

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged

@Stable
internal class InputBarState(
    initialText: String = "",
) {
    var text by mutableStateOf(initialText)
        private set

    var isFocused by mutableStateOf(false)
        private set

    var resetGeneration by mutableStateOf(0)
        private set

    val focusRequester = FocusRequester()

    fun onTextChange(value: String) {
        text = value
    }

    fun onFocusChange(focusState: FocusState) {
        isFocused = focusState.isFocused
    }

    fun clear() {
        text = ""
        resetGeneration++
    }

    fun clearFocus() {
        isFocused = false
        resetGeneration++
    }

    fun focusModifier(): Modifier = Modifier.onFocusChanged(::onFocusChange)

    fun keyboardActions(onSubmit: () -> Unit): KeyboardActions =
        KeyboardActions(onDone = { onSubmit() })
}