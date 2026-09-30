package app.lade.draft.internal.chat.chip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draft.internal.chat.chip.domain.ChipModel
import app.lade.draft.internal.chat.chip.domain.ChipStates
import app.lade.draft.internal.chat.parse.ChipEngine
import app.lade.draft.internal.domain.DraftIntent
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.ui.bar.DraftStateHolder
import app.lade.draft.internal.input.BarInputControlPort
import app.lade.draftdata.DraftModel
import app.lade.humanize.api.Humanize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ChipViewModel @Inject constructor(
    private val store: DraftStore,
    private val engine: ChipEngine,
    private val session: ChipSession,
    private val barState: DraftStateHolder,
    private val input: BarInputControlPort,
    humanize: Humanize,
) : ViewModel() {

    private val area = ChipArea(
        inPort = object : ChipInPort {
            override val model: Flow<DraftModel> = store.state.map { it.draft }
            override val humanize: Humanize = humanize
        },
        outPort = object : ChipOutPort {
            override val onEdit: (ChipKey) -> Unit = ::handleEdit
            override val onEditExit: () -> Unit = ::handleEditExit
            override val onRemove: (ChipKey) -> Unit = ::handleRemove
            override val onPendingChange: (Boolean) -> Unit = { /* submit visibility */ }
        },
        session = session,
        scope = viewModelScope,
    )

    val entryChips: StateFlow<List<ChipModel>> = area.entryChips
    val goalChips: StateFlow<List<ChipModel>> = area.goalChips
    val alarmChips: StateFlow<List<ChipModel>> = area.alarmChips
    val chipStates: StateFlow<ChipStates> = area.chipStates

    init {
        viewModelScope.launch {
            var previous = engine.collectKeys(store.state.value.draft)
            store.state.map { it.draft }.collect { draft ->
                val current = engine.collectKeys(draft)
                val added = current - previous
                if (added.isNotEmpty()) area.onPropose(added)
                previous = current
            }
        }
    }

    fun onCommitAll() = area.onCommitAll()

    fun onClear() = area.onClear()

    private fun handleEdit(key: ChipKey) {
        barState.enterEdit()
    }

    private fun handleEditExit() {
        input.setPrefill("")
    }

    private fun handleRemove(key: ChipKey) {
        store.dispatch(DraftIntent.Update { engine.remove(it, key) })
        input.setPrefill("")
    }
}