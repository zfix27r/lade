package app.lade.draft.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class DraftBarLifecycle(
    scope: CoroutineScope,
    private val hasChanges: StateFlow<Boolean>,
    isFocused: Flow<Boolean>,
) {

    private val prompt = MutableStateFlow(false)

    val state: StateFlow<DraftBarLifecycleState> = combine(
        hasChanges,
        isFocused,
        prompt,
    ) { changes, focused, promptVisible ->
        DraftBarLifecycleState(
            expanded = focused,
            hasChanges = changes,
            promptVisible = promptVisible,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = DraftBarLifecycleState(),
    )

    fun onBarTapped() {
        if (hasChanges.value) {
            prompt.value = true
        }
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