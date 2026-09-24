package app.lade.draftdata

sealed interface DraftEvent {
    data class Saved(val entryId: Long) : DraftEvent
    data class Error(val error: Any) : DraftEvent
    data object Cancelled : DraftEvent
    data object OpenRequested : DraftEvent
}