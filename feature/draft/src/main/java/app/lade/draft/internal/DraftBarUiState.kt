package app.lade.draft.internal

import app.lade.draft.internal.bar.BarMode

internal data class DraftBarUiState(
    val phase: DraftPhase = DraftPhase.IDLE,
    val expanded: Boolean = false,
    val hasChanges: Boolean = false,
    val promptVisible: Boolean = false,
    val mode: BarMode = BarMode.Chat,
)