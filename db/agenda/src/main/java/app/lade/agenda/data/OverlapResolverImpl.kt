package app.lade.agenda.data

import app.lade.agenda.api.OverlapResolver
import app.lade.agenda.api.entry.EntryModel as ApiEntryModel
import app.lade.agenda.api.overlap.Overlap
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapInterval
import app.lade.agenda.api.overlap.OverlapResult
import app.lade.agenda.data.day.DayProject
import app.lade.agenda.data.entry.toApi
import app.lade.agenda.data.log.toApi
import app.lade.agenda.data.overlap.OverlapSplitResult
import app.lade.entrystore.entry.EntryDao
import app.lade.entrystore.log.LogDao
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlapResolverImpl @Inject constructor(
    private val entryDao: EntryDao,
    private val logDao: LogDao,
    private val dayProject: DayProject,
) : OverlapResolver {

    override suspend fun findOverlaps(
        entryId: Long,
        start: LocalTime,
        end: LocalTime,
        date: LocalDate,
    ): List<Overlap> {
        val entries = entryDao.observeActive().first()
        val logs = logDao.observeByDay(date.toEpochDay()).first()
        val plan = dayProject.project(
            date = date,
            entries = entries.map { it.toApi() },
            logs = logs.map { it.toApi() },
        )
        val candidate = OverlapInterval(entryId, start, end)
        return plan.timed
            .filter { it.entryId != entryId && it.hasTime }
            .mapNotNull { slot ->
                val other = OverlapInterval(
                    entryId = slot.entryId,
                    start = slot.start!!,
                    end = slot.end!!,
                    title = slot.title,
                )
                when {
                    contains(candidate, other) -> Overlap(
                        covering = candidate,
                        covered = other,
                    )
                    contains(other, candidate) -> Overlap(
                        covering = other,
                        covered = candidate,
                    )
                    else -> null
                }
            }
    }

    override suspend fun resolveOverlap(
        choice: OverlapChoice,
        overlap: Overlap,
        coveringEntry: ApiEntryModel,
    ): OverlapResult {
        return when (choice) {
            OverlapChoice.DELETE_COVERED -> {
                val coveredId = overlap.covered.entryId
                val target = entryDao.getById(coveredId)
                    ?: error("covered Entry $coveredId missing")
                entryDao.update(target.copy(archivedAtEpochMs = System.currentTimeMillis()))
                OverlapResult.DeletedCovered(coveredId)
            }
            OverlapChoice.SPLIT_COVERING -> {
                val split = splitEntry(
                    covering = coveringEntry,
                    cutStart = overlap.covered.start,
                    cutEnd = overlap.covered.end,
                )
                val primaryId = entryDao.insert(split.primary.toEntity())
                val secondaryId = split.secondary?.let { entryDao.insert(it.toEntity()) }
                OverlapResult.SplitApplied(primaryId, secondaryId)
            }
        }
    }

    private fun contains(outer: OverlapInterval, inner: OverlapInterval): Boolean {
        if (outer.end <= outer.start || inner.end <= inner.start) return false
        return !outer.start.isAfter(inner.start) && !outer.end.isBefore(inner.end)
    }

    private fun splitEntry(
        covering: ApiEntryModel,
        cutStart: LocalTime,
        cutEnd: LocalTime,
    ): OverlapSplitResult {
        val start = covering.startTime ?: error("covering Entry needs startTime")
        val end = covering.endTime ?: error("covering Entry needs endTime")
        val hasLeft = cutStart > start
        val hasRight = cutEnd < end
        return when {
            hasLeft && hasRight -> OverlapSplitResult(
                primary = covering.copy(endTime = cutStart),
                secondary = covering.copy(
                    id = 0,
                    startTime = cutEnd,
                    endTime = end,
                    createdAtEpochMs = System.currentTimeMillis(),
                ),
            )
            hasLeft -> OverlapSplitResult(covering.copy(endTime = cutStart), null)
            else -> OverlapSplitResult(covering.copy(startTime = cutEnd), null)
        }
    }
}