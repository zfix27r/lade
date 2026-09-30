package app.lade.draft.internal.input

import kotlinx.coroutines.flow.SharedFlow

internal interface BarInputControlPort {
    val clickSubmit: SharedFlow<Unit>
    val changeText: SharedFlow<String>
    fun setPrefill(text: String?)
    fun setSubmitVisible(visible: Boolean)
}