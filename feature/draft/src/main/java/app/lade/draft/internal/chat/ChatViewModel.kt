package app.lade.draft.internal.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.chat.api.ParserModel
import app.lade.draft.internal.chat.chip.ChipArea
import app.lade.draft.internal.chat.chip.ChipEngine
import app.lade.draft.internal.chat.chip.ChipInPort
import app.lade.draft.internal.chat.chip.ChipOutPort
import app.lade.draft.internal.chat.chip.data.ChipKey
import app.lade.draft.internal.chat.chip.data.ChipModel
import app.lade.draft.internal.chat.input.ParserRequestFactory
import app.lade.draft.internal.domain.DraftIntent
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.input.InputInPort
import app.lade.draft.internal.input.InputOutPort
import app.lade.draft.internal.input.domain.BarInputAction
import app.lade.draft.internal.input.domain.BarInputState
import app.lade.draft.internal.ui.bar.BarStateHolder
import app.lade.draftdata.DraftModel
import app.lade.humanize.api.Humanize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val store: DraftStore,
    private val engine: ChipEngine,
    private val barState: BarStateHolder,
    private val inputIn: InputInPort,
    private val inputOut: InputOutPort,
    humanize: Humanize,
) : ViewModel() {

    private val stringifier = DraftStringifier(humanize)

    private val pending = MutableStateFlow(false)

    private val chipArea = ChipArea(
        inPort = object : ChipInPort {
            override val model: Flow<DraftModel> = store.state.map { it.draft }
            override val humanize: Humanize = humanize
        },
        outPort = object : ChipOutPort {
            override val onEdit: (ChipKey) -> Unit = ::handleEdit
            override val onEditExit: () -> Unit = ::handleEditExit
            override val onRemove: (ChipKey) -> Unit = ::handleRemove
            override val onPendingChange: (Boolean) -> Unit = { pending.value = it }
        },
        scope = viewModelScope,
    )

    val entryChips: StateFlow<List<ChipModel>> = chipArea.entryChips
    val goalChips: StateFlow<List<ChipModel>> = chipArea.goalChips
    val alarmChips: StateFlow<List<ChipModel>> = chipArea.alarmChips

    private var parseJob: Job? = null

    init {
        inputOut.observe(::onValueChange)
        inputOut.observeAction(::onAction)

        viewModelScope.launch {
            barState.state
                .map { it.resetGeneration }
                .distinctUntilChanged()
                .collect { gen ->
                    if (gen > 0) {
                        chipArea.onClear()
                        inputIn.setPrefill(null)
                    }
                }
        }

        viewModelScope.launch {
            combine(store.state, pending) { state, p ->
                p to !state.draft.isEmpty
            }.collect { (p, hasDraft) ->
                updateAction(p, hasDraft)
            }
        }
    }

    private fun onAction() {
        if (pending.value) onCommit() else onSave()
    }

    fun onCommit() {
        chipArea.onCommitAll()
    }

    fun onSave() {
        viewModelScope.launch {
            store.dispatch(DraftIntent.Save)
            barState.enterIdle()
        }
    }

    private fun onValueChange(text: String) {
        parseJob?.cancel()
        val editing = chipArea.editingKey
        val request = ParserRequestFactory.of(
            raw = text,
            editing = editing,
            states = chipArea.chipStates.value,
            draft = store.state.value.draft,
        )
        parseJob = viewModelScope.launch {
            delay(DEBOUNCE_MS.milliseconds)
            val result = engine.parse(request)
            applyResult(result)
        }
    }

    private fun applyResult(result: ParserModel) {
        val before = engine.collectKeys(store.state.value.draft)
        store.dispatch(DraftIntent.Update { engine.merge(it, result) })
        val editing = chipArea.editingKey
        if (editing == null) {
            val after = engine.collectKeys(store.state.value.draft)
            chipArea.onPropose(after - before)
        }
    }

    private fun handleEdit(key: ChipKey) {
        barState.enterEdit()
        val raw = stringifier.stringify(key.kind, store.state.value.draft, key.index)
        inputIn.setPrefill(raw)
    }

    private fun handleEditExit() {
        inputIn.setPrefill("")
    }

    private fun handleRemove(key: ChipKey) {
        store.dispatch(DraftIntent.Update { engine.remove(it, key) })
        inputIn.setPrefill("")
    }

    private fun updateAction(pending: Boolean, hasDraft: Boolean) {
        inputIn.setAction(
            when {
                pending -> BarInputState(
                    action = BarInputAction.Commit,
                    actionEnabled = true,
                )
                hasDraft -> BarInputState(
                    action = BarInputAction.Send,
                    actionEnabled = true,
                )
                else -> BarInputState()
            }
        )
    }

    override fun onCleared() {
        inputOut.clear()
    }

    private companion object {
        const val DEBOUNCE_MS = 400L
    }
}