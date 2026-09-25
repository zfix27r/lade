package app.lade.draft.internal.ui

import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.draft.api.DraftPhase
import app.lade.draft.internal.domain.DraftEffect
import app.lade.draft.internal.domain.DraftIntent
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.ui.bar.BarMode
import app.lade.draft.internal.ui.bar.BarState
import app.lade.draft.internal.ui.bar.BarStateHolder
import app.lade.draft.internal.ui.bar.DraftBarEvent
import app.lade.draftdata.DraftModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class DraftBarViewModel @Inject constructor(
    private val store: DraftStore,
    private val barState: BarStateHolder,
) : ViewModel() {

    val bar: StateFlow<BarState> = barState.state

    val draft: StateFlow<DraftModel> = store.state
        .map { it.draft }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DraftModel())

    private val lifecycle = DraftBarLifecycle(
        scope = viewModelScope,
        phase = barState.phase,
    )
    val lifecycleState: StateFlow<DraftBarLifecycleState> = lifecycle.state

    init {
        viewModelScope.launch {
            store.effects.collect { effect ->
                when (effect) {
                    is DraftEffect.OpenRequested -> {
                        lifecycle.onDismiss()
                        barState.selectMode(BarMode.Chat)
                        barState.updateRawInput(effect.draft.title)
                        barState.enterEdit()
                    }

                    else -> Unit
                }
            }
        }
    }

    fun setDefaultDate(date: LocalDate?) {
        store.dispatch(DraftIntent.SetDefaultDate(date))
    }

    fun onRawInputChange(text: String) {
        barState.updateRawInput(text)
    }

    fun onBarFocusChange(focusState: FocusState) {
        val wasIdle = barState.phase.value == DraftPhase.IDLE
        barState.onFocusChange(focusState)
        if (wasIdle && focusState.isFocused) {
            val pending = store.state.value.pending
            val original = store.state.value.original
            val hasPendingChanges = pending != null && !pending.isEmpty &&
                    (original == null || pending != original)
            if (hasPendingChanges) {
                lifecycle.showPrompt()
            }
        }
    }

    fun onEvent(event: DraftBarEvent) {
        when (event) {
            is DraftBarEvent.TextChange -> lifecycle.onTextChanged()
            DraftBarEvent.Submit -> Unit
            DraftBarEvent.Cancel -> onCancel()
            DraftBarEvent.KindClick -> Unit
            DraftBarEvent.Resume -> onResumeClick()
            DraftBarEvent.DismissResume -> onDismissResume()
            is DraftBarEvent.ChipClick -> Unit
            is DraftBarEvent.ChipRemove -> Unit
            DraftBarEvent.ModeSwitch -> barState.nextMode()
        }
    }

    private fun onResumeClick() {
        store.dispatch(DraftIntent.RestorePending)
        lifecycle.onResume()
        barState.enterEdit()
    }

    private fun onDismissResume() {
        store.dispatch(DraftIntent.ClearPending)
        lifecycle.onDismiss()
    }

    private fun onCancel() {
        val state = store.state.value
        val hasChanges = if (state.original != null) {
            state.draft != state.original
        } else {
            !state.draft.isEmpty
        }
        if (hasChanges) {
            store.dispatch(DraftIntent.StashAndReset)
        } else {
            store.dispatch(DraftIntent.Reset)
        }
        lifecycle.hidePrompt()
        barState.enterIdle()
    }
}