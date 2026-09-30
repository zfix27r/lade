package app.lade.draft.internal.input

import app.lade.draft.api.DraftPhase
import app.lade.draft.internal.input.BarInputControlPort
import app.lade.draft.internal.input.BarInputPort
import app.lade.draft.internal.ui.bar.DraftStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BarInputPortImpl @Inject constructor(
    private val draftStateHolder: DraftStateHolder,
    scope: CoroutineScope,
) : BarInputPort, BarInputControlPort {

    private val _prefill = MutableStateFlow<String?>(null)
    override val prefill: StateFlow<String?> = _prefill.asStateFlow()

    private val _submitVisible = MutableStateFlow(false)
    override val submitVisible: StateFlow<Boolean> = _submitVisible.asStateFlow()

    private val _clickSubmit = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val clickSubmit: SharedFlow<Unit> = _clickSubmit.asSharedFlow()

    private val _changeText = MutableSharedFlow<String>(extraBufferCapacity = 1)
    override val changeText: SharedFlow<String> = _changeText.asSharedFlow()

    init {
        scope.launch {
            draftStateHolder.phase.collect { phase ->
                if (phase == DraftPhase.IDLE) setDefault()
            }
        }
    }

    override fun onClickSubmit() {
        _clickSubmit.tryEmit(Unit)
    }

    override fun onChangeText(text: String) {
        _changeText.tryEmit(text)
    }

    override fun setPrefill(text: String?) {
        _prefill.value = text
    }

    override fun setSubmitVisible(visible: Boolean) {
        _submitVisible.value = visible
    }

    private fun setDefault() {
        _prefill.value = ""
        _submitVisible.value = false
    }
}