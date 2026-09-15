package app.lade.agenda.data

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalError
import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.log.LogError
import app.lade.agenda.api.log.LogSaveModel
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapError
import app.lade.agenda.api.overlap.OverlapModel
import app.lade.agenda.api.overlap.OverlapResult
import app.lade.agenda.api.series.SeriesEditDraft
import app.lade.agenda.api.series.SeriesEditScope
import app.lade.agenda.api.series.SeriesError
import app.lade.agenda.data.agenda.AgendaComposer
import app.lade.agenda.data.entry.EntryStore
import app.lade.agenda.data.entry.EntryValidator
import app.lade.agenda.data.goal.GoalStore
import app.lade.agenda.data.goal.GoalValidator
import app.lade.agenda.data.log.LogStore
import app.lade.agenda.data.log.LogValidator
import app.lade.agenda.data.overlap.OverlapStore
import app.lade.agenda.data.overlap.OverlapValidator
import app.lade.agenda.data.series.SeriesStore
import app.lade.agenda.data.series.SeriesValidator
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgendaApiImpl @Inject constructor(
    private val composer: AgendaComposer,
    private val entryStore: EntryStore,
    private val entryValidator: EntryValidator,
    private val goalStore: GoalStore,
    private val goalValidator: GoalValidator,
    private val logStore: LogStore,
    private val logValidator: LogValidator,
    private val overlapStore: OverlapStore,
    private val overlapValidator: OverlapValidator,
    private val seriesStore: SeriesStore,
    private val seriesValidator: SeriesValidator,
) : AgendaApi {

    // === Agenda ===

    override suspend fun get(entryId: Long, date: LocalDate): AgendaModel? =
        composer.get(entryId, date)

    override fun observeList(date: LocalDate): Flow<List<AgendaModel>> =
        composer.observeList(date)

    override fun observeRange(
        from: LocalDate,
        to: LocalDate,
    ): Flow<List<AgendaModel>> = composer.observeRange(from, to)

    // === Entry ===
    override fun observeAllEntries(): Flow<List<EntryModel>> =
        entryStore.observeAll()

    override fun observeArchivedEntries(): Flow<List<EntryModel>> =
        entryStore.observeArchived()
    
    override suspend fun saveEntry(entry: EntryModel): Result<Long, EntryError> {
        entryValidator.validate(entry)?.let { return Result.Failure(it) }
        return Result.Success(entryStore.save(entry))
    }

    override suspend fun archiveEntry(entryId: Long): Result<Unit, EntryError> {
        entryStore.archive(entryId)
        return Result.Success(Unit)
    }

    // === Goal ===

    override suspend fun saveGoals(
        entryId: Long,
        goals: List<GoalModel>,
    ): Result<Unit, GoalError> {
        goalValidator.validate(goals)?.let { return Result.Failure(it) }
        goalStore.save(entryId, goals)
        return Result.Success(Unit)
    }

    // === Log ===

    override suspend fun saveLogs(saveModel: LogSaveModel): Result<Unit, LogError> {
        logValidator.validate(saveModel)?.let { return Result.Failure(it) }
        logStore.save(saveModel, saveModel.origin.storage)
        return Result.Success(Unit)
    }

    // === Overlap ===

    override suspend fun resolveOverlap(
        choice: OverlapChoice,
        overlapModel: OverlapModel,
        coveringEntry: EntryModel,
    ): Result<OverlapResult, OverlapError> {
        overlapValidator.validate(choice, overlapModel, coveringEntry)
            ?.let { return Result.Failure(it) }
        return Result.Success(overlapStore.resolve(choice, overlapModel, coveringEntry))
    }

    // === Series ===

    override suspend fun applySeriesEdit(
        entryId: Long,
        date: LocalDate,
        scope: SeriesEditScope,
        draft: SeriesEditDraft,
    ): Result<Long, SeriesError> {
        val entry = entryStore.getById(entryId)
            ?: return Result.Failure(SeriesError.EntryMissing)
        seriesValidator.validate(entry, scope, draft)
            ?.let { return Result.Failure(it) }
        return Result.Success(seriesStore.apply(entry, date, scope, draft))
    }
}