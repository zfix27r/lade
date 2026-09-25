package app.lade.draft.internal.ui.bar

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged

internal data class BarState(
    val mode: BarMode = BarMode.Chat,
    val rawInput: String = "",
    val resetGeneration: Int = 0,
    val focusRequestGeneration: Int = 0,
    val keyboardRequestGeneration: Int = 0,
)

internal fun Modifier.barFocus(onFocusChange: (FocusState) -> Unit): Modifier =
    onFocusChanged(onFocusChange)