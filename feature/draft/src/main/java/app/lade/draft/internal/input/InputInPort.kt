package app.lade.draft.internal.input

import app.lade.draft.internal.input.domain.BarInputState
import kotlinx.coroutines.flow.StateFlow

internal interface InputInPort {
    val prefill: StateFlow<String?>
    val action: StateFlow<BarInputState>
    fun setPrefill(text: String?)
    fun setAction(state: BarInputState)
}