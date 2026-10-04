package app.lade.draft.internal.input.domain

import app.lade.draft.internal.input.BarInputSubmit
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal interface BarInputControlPort {
    val prefill: StateFlow<String?>
    val submit: StateFlow<BarInputSubmit>
    val clearInput: SharedFlow<Unit>

    fun onClickSubmit()
    fun onChangeText(text: String)
}