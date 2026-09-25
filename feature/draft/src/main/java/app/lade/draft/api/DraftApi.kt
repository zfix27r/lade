package app.lade.draft.api

import kotlinx.coroutines.flow.StateFlow

interface DraftApi {
    val phase: StateFlow<DraftPhase>
    fun open(entryId: Long?)
    fun reset()
}