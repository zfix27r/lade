package app.lade.agenda.api

import app.lade.agenda.api.agenda.AgendaError
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.agenda.AgendaSaveModel
import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalError
import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.log.LogError
import app.lade.agenda.api.log.LogSaveModel
import app.lade.agenda.api.overlap.OverlapModel
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapError
import app.lade.agenda.api.overlap.OverlapResult
import app.lade.agenda.api.series.SeriesEditDraft
import app.lade.agenda.api.series.SeriesEditScope
import app.lade.agenda.api.series.SeriesError
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AgendaApi {
    // Agenda
    suspend fun get(entryId: Long, date: LocalDate?): AgendaModel?
    fun observeList(date: LocalDate): Flow<List<AgendaModel>>
    fun observeRange(from: LocalDate, to: LocalDate): Flow<List<AgendaModel>>
    suspend fun saveAgenda(model: AgendaSaveModel): Result<Long, AgendaError>
    suspend fun restoreEntry(entryId: Long): Result<Unit, EntryError>
    // Entry
    fun observeAllEntries(): Flow<List<EntryModel>>
    fun observeArchivedEntries(): Flow<List<EntryModel>>
    suspend fun saveEntry(entry: EntryModel): Result<Long, EntryError>
    suspend fun archiveEntry(entryId: Long): Result<Unit, EntryError>
    // Goal
    suspend fun saveGoals(entryId: Long, goals: List<GoalModel>): Result<Unit, GoalError>

    // Log
    suspend fun saveLogs(saveModel: LogSaveModel): Result<Unit, LogError>

    // Overlap
    suspend fun resolveOverlap(
        choice: OverlapChoice,
        overlapModel: OverlapModel,
        coveringEntry: EntryModel,
    ): Result<OverlapResult, OverlapError>

    // Series
    suspend fun applySeriesEdit(
        entryId: Long,
        date: LocalDate,
        scope: SeriesEditScope,
        draft: SeriesEditDraft,
    ): Result<Long, SeriesError>
}