package app.lade.draft.internal

internal data class DraftBarLifecycleState(
    val expanded: Boolean = false,
    val hasChanges: Boolean = false,
    val promptVisible: Boolean = false,
)