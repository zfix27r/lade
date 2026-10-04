package app.lade.draft.internal.chat.chip

import app.lade.draft.api.DraftPhase
import app.lade.draft.internal.chat.chip.domain.ChipBuilder
import app.lade.draft.internal.chat.chip.domain.ChipController
import app.lade.draft.internal.chat.chip.domain.ChipData
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draft.internal.chat.chip.domain.ChipStates
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.ui.bar.DraftStateHolder
import app.lade.humanize.api.Humanize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChipPortImpl @Inject constructor(
    private val controller: ChipController,
    private val draftStore: DraftStore,
    private val draftStateHolder: DraftStateHolder,
    humanize: Humanize,
) : ChipOutPort, ChipControlPort {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val builder = ChipBuilder(humanize)

    private val _states = MutableStateFlow(ChipStates())
    override val states: StateFlow<ChipStates> = _states.asStateFlow()

    private val _chips = MutableStateFlow<List<ChipData>>(emptyList())
    override val chips: StateFlow<List<ChipData>> = _chips.asStateFlow()

    private val _clickRemove = MutableSharedFlow<ChipKey>(extraBufferCapacity = 8)
    override val clickRemove: SharedFlow<ChipKey> = _clickRemove.asSharedFlow()

    init {
        scope.launch {
            draftStore.state
                .map { it.draft }
                .distinctUntilChanged()
                .collect { rebuild() }
        }

        scope.launch {
            draftStateHolder.phase.collect { rebuild() }
        }
    }

    private fun rebuild() {
        if (draftStateHolder.isIdle()) {
            _chips.value = emptyList()
            return
        }
        _chips.value = builder.build(draftStore.state.value.draft)
    }

    override fun onClickEdit(key: ChipKey) {
        if (draftStateHolder.isIdle()) return
        _states.value = controller.onTap(_states.value, key)
    }

    override fun onClickRemove(key: ChipKey) {
        if (draftStateHolder.isIdle()) return
        _states.value = controller.onRemove(_states.value, key)
        _clickRemove.tryEmit(key)
    }

    override fun onPropose(keys: Set<ChipKey>) {
        if (draftStateHolder.isIdle()) return
        if (keys.isEmpty()) return
        _states.value = controller.onPropose(_states.value, keys)
    }

    override fun onCommitAll() {
        if (draftStateHolder.phase.value != DraftPhase.EDIT) return
        _states.value = controller.onCommitAll(_states.value)
    }

    override fun onClear() {
        _states.value = controller.onClear(_states.value)
    }
}