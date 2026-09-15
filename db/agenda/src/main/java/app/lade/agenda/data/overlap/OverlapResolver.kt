package app.lade.agenda.data.overlap

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.overlap.OverlapModel
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapResult
import app.lade.agenda.data.entry.toEntity
import app.lade.agendastore.entry.EntryDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlapResolver @Inject constructor(
    private val entryDao: EntryDao,
    private val splitter: OverlapSplitter,
) {
    suspend fun resolve(
        choice: OverlapChoice,
        overlapModel: OverlapModel,
        coveringEntry: EntryModel,
    ): OverlapResult {
        return when (choice) {
            OverlapChoice.DELETE_COVERED -> {
                val coveredId = overlapModel.covered.entryId
                val target = entryDao.getById(coveredId)
                    ?: error("covered Entry $coveredId missing")
                entryDao.update(target.copy(archivedAtEpochMs = System.currentTimeMillis()))
                OverlapResult.DeletedCovered(coveredId)
            }
            OverlapChoice.SPLIT_COVERING -> {
                val split = splitter.split(coveringEntry, overlapModel.covered.start, overlapModel.covered.end)
                val primaryId = entryDao.insert(split.primary.toEntity())
                val secondaryId = split.secondary?.let { entryDao.insert(it.toEntity()) }
                OverlapResult.SplitApplied(primaryId, secondaryId)
            }
        }
    }
}