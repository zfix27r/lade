package app.lade.draft.internal.input

import kotlinx.coroutines.flow.SharedFlow

internal interface BarInputPort {
    val clickSubmit: SharedFlow<Unit>
    val changeText: SharedFlow<String>

    fun setPrefill(text: String?)
    fun setSubmit(submit: BarInputSubmit)
    fun clearInput()
}