package app.lade.draft.internal.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.draft.api.DraftPhase
import app.lade.draft.internal.chat.chip.ChipOutsidePort
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draft.internal.chat.parse.ParseEngine
import app.lade.draft.internal.chat.parse.ParseStringifier
import app.lade.draft.internal.chat.parse.ParserRequestFactory
import app.lade.draft.internal.domain.DraftIntent
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.input.BarInputPort
import app.lade.draft.internal.input.BarInputSubmit
import app.lade.draft.internal.ui.bar.DraftStateHolder
import app.lade.humanize.api.Humanize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val store: DraftStore,
    private val engine: ParseEngine,
    private val chips: ChipOutsidePort,
    private val barState: DraftStateHolder,
    private val input: BarInputPort,
    humanize: Humanize,
) : ViewModel() {

    private val stringifier = ParseStringifier(humanize)
    private var collectorJob: Job? = null

    init {
        viewModelScope.launch {
            barState.phase.collect { phase ->
                when (phase) {
                    DraftPhase.EDIT -> startCollectors()
                    DraftPhase.IDLE -> stopCollectors()
                }
            }
        }
    }

    private fun startCollectors() {
        if (collectorJob?.isActive == true) return
        collectorJob = viewModelScope.launch {
            launch {
                input.changeText
                    .debounce(DEBOUNCE_MS.milliseconds)
                    .distinctUntilChanged()
                    .collect(::onValueChange)
            }
            launch {
                input.clickSubmit.collect { onSubmit() }
            }
            launch {
                chips.clickRemove.collect(::onChipRemove)
            }
            launch {
                chips.states
                    .map { it.editingKey }
                    .distinctUntilChanged()
                    .collect(::onEditingChange)
            }
            launch {
                chips.states
                    .map { it.hasActive }
                    .distinctUntilChanged()
                    .collect(::onPendingChange)
            }
        }
    }

    private fun stopCollectors() {
        collectorJob?.cancel()
        collectorJob = null
        chips.onClear()
    }

    private fun onValueChange(text: String) {
        if (text.isBlank()) return
        if (barState.phase.value == DraftPhase.IDLE) return

        val states = chips.states.value
        val editing = states.editingKey
        val draft = store.state.value.draft

        val request = ParserRequestFactory.of(
            raw = text,
            editing = editing,
            states = states,
            draft = draft,
        )

        viewModelScope.launch {
            val result = engine.parse(request)

            if (barState.phase.value == DraftPhase.IDLE) return@launch

            val before = engine.collectKeys(store.state.value.draft)
            store.dispatch(DraftIntent.Update { engine.merge(it, result) })

            if (editing == null) {
                val after = engine.collectKeys(store.state.value.draft)
                chips.onPropose(after - before)
            }
        }
    }

    private fun onSubmit() {
        if (chips.states.value.hasActive) {
            chips.onCommitAll()
            input.clearInput()
        } else {
            viewModelScope.launch {
                store.dispatch(DraftIntent.Save)
                barState.enterIdle()
            }
        }
    }

    private fun onEditingChange(editing: ChipKey?) {
        if (editing == null) {
            input.setPrefill("")
        } else {
            val raw = stringifier.stringify(
                editing.kind,
                store.state.value.draft,
                editing.index,
            )
            input.setPrefill(raw)
        }
    }

    private fun onPendingChange(hasActive: Boolean) {
        val hasDraft = !store.state.value.draft.isEmpty
        val submit = when {
            hasActive -> BarInputSubmit.Commit
            hasDraft -> BarInputSubmit.Send
            else -> BarInputSubmit.None
        }
        input.setSubmit(submit)
    }

    private fun onChipRemove(key: ChipKey) {
        store.dispatch(DraftIntent.Update { engine.remove(it, key) })
        input.setPrefill("")
    }

    private companion object {
        const val DEBOUNCE_MS = 400L
    }
}