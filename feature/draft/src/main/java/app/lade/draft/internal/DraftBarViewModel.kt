package app.lade.draft.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.draft.internal.bar.BarMode
import app.lade.draft.internal.bar.DraftBarEvent
import app.lade.draft.internal.store.DraftStore
import app.lade.draftdata.DraftEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class DraftBarViewModel @Inject constructor(
    private val store: DraftStore,
) : ViewModel() {

    val barState = store.barState
    val draft = store.draft
    val hasChanges = store.hasChanges

    private val lifecycle = DraftBarLifecycle(
        scope = viewModelScope,
        hasChanges = store.hasChanges,
        isFocused = store.barState.state.map { it.isFocused },
    )
    val lifecycleState = lifecycle.state

    init {
        viewModelScope.launch {
            store.events.collect { event ->
                when (event) {
                    DraftEvent.OpenRequested -> {
                        lifecycle.onDismiss()
                        barState.selectMode(BarMode.Chat)
                        barState.updateRawInput(store.draft.value.title)
                        barState.requestFocus()
                    }
                    else -> Unit
                }
            }
        }
    }

    fun setDefaultDate(date: LocalDate?) {
        store.setDefaultDate(date)
    }

    fun onEvent(event: DraftBarEvent) {
        when (event) {
            is DraftBarEvent.TextChange -> lifecycle.onTextChanged()
            DraftBarEvent.Submit -> Unit
            DraftBarEvent.Cancel -> onCancel()
            DraftBarEvent.KindClick -> Unit
            DraftBarEvent.BarTapped -> onBarTapped()
            DraftBarEvent.Resume -> onResumeClick()
            DraftBarEvent.DismissResume -> onDismissResume()
            is DraftBarEvent.ChipClick -> Unit
            is DraftBarEvent.ChipRemove -> Unit
            DraftBarEvent.ModeSwitch -> barState.nextMode()
        }
    }

    private fun onBarTapped() {
        if (hasChanges.value) {
            lifecycle.onBarTapped()
            store.stashAndReset()
            barState.requestFocus()
        } else {
            barState.requestFocus()
        }
    }

    private fun onResumeClick() {
        store.restorePending()
        lifecycle.onResume()
        barState.requestFocus()
    }

    private fun onDismissResume() {
        store.clearPending()
        lifecycle.onDismiss()
        store.reset()
        barState.clearInput()
        barState.requestFocus()
    }

    private fun onCancel() {
        barState.clearFocus()
        barState.clearInput()
    }
}