package app.lade.draft.internal.input.domain

import app.lade.draft.internal.input.InputInPort
import app.lade.draft.internal.input.InputOutPort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BarInputHolder @Inject constructor() : InputInPort, InputOutPort {

    private val _prefill = MutableStateFlow<String?>(null)
    override val prefill: StateFlow<String?> = _prefill.asStateFlow()

    private val _action = MutableStateFlow(BarInputState())
    override val action: StateFlow<BarInputState> = _action.asStateFlow()

    private var valueListener: ((String) -> Unit)? = null
    private var actionListener: (() -> Unit)? = null

    override fun setPrefill(text: String?) {
        _prefill.value = text
    }

    override fun setAction(state: BarInputState) {
        _action.value = state
    }

    override fun onValueChange(text: String) {
        valueListener?.invoke(text)
    }

    override fun onActionClick() {
        actionListener?.invoke()
    }

    override fun observe(block: (String) -> Unit) {
        valueListener = block
    }

    override fun observeAction(block: () -> Unit) {
        actionListener = block
    }

    override fun clear() {
        valueListener = null
        actionListener = null
    }
}