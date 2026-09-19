package app.lade.draft

import app.lade.agenda.api.agenda.AgendaError

sealed interface DraftEvent {
    data class Saved(val entryId: Long) : DraftEvent
    data class Error(val error: AgendaError) : DraftEvent
    data class OpenEditor(val entryId: Long?) : DraftEvent
    data object Cancelled : DraftEvent
}