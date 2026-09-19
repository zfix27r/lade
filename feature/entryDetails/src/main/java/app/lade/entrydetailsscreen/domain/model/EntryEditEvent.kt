package app.lade.entrydetailsscreen.domain.model

import app.lade.agenda.api.agenda.AgendaError
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.overlap.OverlapModel

sealed interface EntryEditEvent {
    data object Saved : EntryEditEvent
    data object Restored : EntryEditEvent
    data object Close : EntryEditEvent
    data class ShowAgendaError(val error: AgendaError) : EntryEditEvent
    data class ShowOverlap(
        val conflict: OverlapModel,
        val pending: EntryModel,
    ) : EntryEditEvent
    data object ShowUnsavedChangesDialog : EntryEditEvent
}