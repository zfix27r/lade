package app.lade.draft.internal.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.draft.internal.chat.parse.ChipEngine
import app.lade.draft.internal.chat.chip.ChipSession
import app.lade.draft.internal.chat.parse.ParserRequestFactory
import app.lade.draft.internal.domain.DraftIntent
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.input.BarInputControlPort
import app.lade.draft.internal.ui.bar.DraftStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val store: DraftStore,
    private val engine: ChipEngine,
    private val session: ChipSession,
    private val barState: DraftStateHolder,
    private val input: BarInputControlPort,
) : ViewModel() {

    private var parseJob: Job? = null

    init {
        viewModelScope.launch {
            input.changeText.collect(::onValueChange)
        }
        viewModelScope.launch {
            barState.phase.collect { phase ->
                if (phase == app.lade.draft.api.DraftPhase.IDLE) {
                    session.onClear()
                }
            }
        }
    }

    private fun onValueChange(text: String) {
        parseJob?.cancel()
        val editing = session.editingKey
        val request = ParserRequestFactory.of(
            raw = text,
            editing = editing,
            states = session.states.value,
            draft = store.state.value.draft,
        )
        parseJob = viewModelScope.launch {
            delay(DEBOUNCE_MS.milliseconds)
            val result = engine.parse(request)
            store.dispatch(DraftIntent.Update { engine.merge(it, result) })
        }
    }

    private companion object {
        const val DEBOUNCE_MS = 400L
    }
}