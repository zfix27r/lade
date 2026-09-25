package app.lade.draft.internal.ui

import app.lade.draft.api.DraftPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class DraftBarLifecycle(
    scope: CoroutineScope,
    phase: Flow<DraftPhase>,
) {

    private val prompt = MutableStateFlow(false)

    val state: StateFlow<DraftBarLifecycleState> = combine(
        phase,
        prompt,
    ) { currentPhase, promptVisible ->
        DraftBarLifecycleState(
            phase = currentPhase,
            promptVisible = promptVisible,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = DraftBarLifecycleState(),
    )

    fun showPrompt() {
        prompt.value = true
    }

    fun hidePrompt() {
        prompt.value = false
    }

    fun onResume() {
        prompt.value = false
    }

    fun onDismiss() {
        prompt.value = false
    }

    fun onTextChanged() {
        prompt.value = false
    }
}