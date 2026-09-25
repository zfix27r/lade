package app.lade.draft.internal.ui

import app.lade.draft.api.DraftPhase

internal data class DraftBarLifecycleState(
    val phase: DraftPhase = DraftPhase.IDLE,
    val promptVisible: Boolean = false,
)