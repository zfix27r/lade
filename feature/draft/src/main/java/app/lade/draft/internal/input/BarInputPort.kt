package app.lade.draft.internal.input

import kotlinx.coroutines.flow.StateFlow

internal interface BarInputPort {
    val prefill: StateFlow<String?>
    val submitVisible: StateFlow<Boolean>
    fun onClickSubmit()
    fun onChangeText(text: String)
}