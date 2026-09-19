package app.lade.entrydetailsscreen.domain

import app.lade.agenda.api.Result
import app.lade.agenda.api.agenda.AgendaError
import app.lade.agenda.api.agenda.AgendaSaveModel
import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapError
import app.lade.agenda.api.overlap.OverlapModel
import app.lade.agenda.api.overlap.OverlapResult
import app.lade.entrydetailsscreen.domain.model.EntryEditData
import java.time.LocalDate

interface EntryEditRepository {
    suspend fun load(entryId: Long, date: LocalDate): EntryEditData?
    suspend fun save(model: AgendaSaveModel): app.lade.agenda.api.Result<Long, AgendaError>
    suspend fun resolveOverlap(
        choice: OverlapChoice,
        conflict: OverlapModel,
        pending: EntryModel,
    ): app.lade.agenda.api.Result<OverlapResult, OverlapError>
    suspend fun restore(entryId: Long): app.lade.agenda.api.Result<Unit, EntryError>
    suspend fun archive(entryId: Long): Result<Unit, EntryError>
}