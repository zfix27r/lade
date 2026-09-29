package app.lade.draft.internal.chat.chip

import app.lade.draft.internal.chat.chip.data.ChipBuilder
import app.lade.draft.internal.chat.chip.data.ChipController
import app.lade.draft.internal.chat.chip.data.ChipKey
import app.lade.draft.internal.chat.chip.data.ChipModel
import app.lade.draft.internal.chat.chip.data.ChipState
import app.lade.draft.internal.chat.chip.data.ChipStates
import app.lade.draft.internal.chat.chip.data.ChipStatesHolder
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
    scope: CoroutineScope,
) {
    private val builder = ChipBuilder(inPort.humanize)
    private val holder = ChipStatesHolder(ChipController())

    val chipStates: StateFlow<ChipStates> = holder.chipStates

    init {
        scope.launch {
            holder.chipStates
                .map { it.proposedKeys.isNotEmpty() || it.editingKey != null }
                .distinctUntilChanged()
                .collect(outPort.onPendingChange)
        }
    }

    val entryChips: StateFlow<List<ChipModel>> = combine(
        inPort.model,
        holder.chipStates,
    ) { model, states ->
        builder.buildEntryChips(model, states, ::onTap, ::onRemove)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val goalChips: StateFlow<List<ChipModel>> = combine(
        inPort.model,
        holder.chipStates,
    ) { model, states ->
        builder.buildGoalChips(model, states, ::onTap, ::onRemove)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val alarmChips: StateFlow<List<ChipModel>> = combine(
        inPort.model,
        holder.chipStates,
    ) { model, states ->
        builder.buildAlarmChips(model, states, ::onTap, ::onRemove)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val editingKey: ChipKey? get() = holder.chipStates.value.editingKey

    fun onPropose(keys: Set<ChipKey>) = holder.onPropose(keys)
    fun onCommitAll() = holder.onCommitAll()
    fun onClear() = holder.onClear()

    private fun onTap(kind: ChipKind, index: Int) {
        val key = ChipKey(kind, index)
        val before = holder.chipStates.value.stateOf(key)
        holder.onTap(key)
        val after = holder.chipStates.value.stateOf(key)
        when {
            before != ChipState.EDITING && after == ChipState.EDITING -> outPort.onEdit(key)
            before == ChipState.EDITING && after != ChipState.EDITING -> outPort.onEditExit()
        }
    }

    private fun onRemove(kind: ChipKind, index: Int) {
        val key = ChipKey(kind, index)
        holder.onRemove(key)
        outPort.onRemove(key)
    }
}