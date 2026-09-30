package app.lade.draft.internal

import app.lade.draft.api.DraftApi
import app.lade.draft.api.DraftPhase
import app.lade.draft.internal.domain.DraftIntent
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.ui.bar.DraftStateHolder
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

internal class DraftApiImpl @Inject constructor(
    private val store: DraftStore,
    private val barState: DraftStateHolder,
) : DraftApi {

    override val phase: StateFlow<DraftPhase> = barState.phase

    override fun open(entryId: Long?) {
        store.dispatch(DraftIntent.Open(entryId))
    }

    override fun reset() {
        store.dispatch(DraftIntent.Reset)
    }
}