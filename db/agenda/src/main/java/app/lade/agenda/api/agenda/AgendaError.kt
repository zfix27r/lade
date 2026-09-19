package app.lade.agenda.api.agenda

import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalError
import app.lade.agenda.api.overlap.OverlapModel

sealed interface AgendaError {
    data class Entry(val error: EntryError) : AgendaError
    data class Goals(val errors: Map<Int, GoalError>) : AgendaError
    data class Overlap(val conflict: OverlapModel, val pending: EntryModel) : AgendaError
    data object Unknown : AgendaError
}