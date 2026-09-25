package app.lade.draft.internal.domain

import app.lade.agenda.api.agenda.AgendaError
import app.lade.draftdata.DraftModel

internal sealed interface DraftEffect {
    data class OpenRequested(val draft: DraftModel) : DraftEffect
    data class Saved(val entryId: Long) : DraftEffect
    data class Error(val error: AgendaError) : DraftEffect
    data object Cancelled : DraftEffect
}