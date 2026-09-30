package app.lade.draft.internal.chat.chip

import app.lade.draft.internal.chat.chip.domain.ChipBuilder
import app.lade.draft.internal.chat.chip.domain.ChipController
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draft.internal.chat.chip.domain.ChipModel
import app.lade.draft.internal.chat.chip.domain.ChipState
import app.lade.draft.internal.chat.chip.domain.ChipStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ChipArea(
    private val inPort: ChipInPort,
    private val outPort: ChipOutPort,
    private val session: ChipSession,
    scope: CoroutineScope,
) {
    private val builder = ChipBuilder(inPort.humanize)

    val chipStates: StateFlow<ChipStates> = session.states

    init {
        scope.launch {
            session.states
                .map { it.proposedKeys.isNotEmpty() || it.editingKey != null }
                .distinctUntilChanged()
                .collect(outPort.onPendingChange)
        }
    }

    val entryChips: StateFlow<List<ChipModel>> = combine(inPort.model, session.states) { model, states ->
        builder.buildEntryChips(model, states, ::onTap, ::onRemove)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val goalChips: StateFlow<List<ChipModel>> = combine(inPort.model, session.states) { model, states ->
        builder.buildGoalChips(model, states, ::onTap, ::onRemove)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val alarmChips: StateFlow<List<ChipModel>> = combine(inPort.model, session.states) { model, states ->
        builder.buildAlarmChips(model, states, ::onTap, ::onRemove)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val editingKey: ChipKey? get() = session.editingKey

    fun onPropose(keys: Set<ChipKey>) = session.onPropose(keys)
    fun onCommitAll() = session.onCommitAll()
    fun onClear() = session.onClear()

    private fun onTap(kind: ChipKind, index: Int) {
        val key = ChipKey(kind, index)
        if (session.onTap(key)) outPort.onEdit(key)
    }

    private fun onRemove(kind: ChipKind, index: Int) {
        val key = ChipKey(kind, index)
        session.onRemove(key)
        outPort.onRemove(key)
    }
}