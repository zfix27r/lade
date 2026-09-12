package app.lade.entry.domain

import app.lade.entry.domain.models.ContainmentChoice
import app.lade.entry.domain.models.ContainmentConflict
import app.lade.entry.domain.models.Entry
import javax.inject.Inject
import javax.inject.Singleton

sealed class ContainmentResolveResult {
	data class DeletedCovered(val coveredId: Long) : ContainmentResolveResult()
	data class SplitApplied(val primaryId: Long, val secondaryId: Long?) : ContainmentResolveResult()
}

/**
 * Applies user choice for a [app.lade.entrydetailsscreen.domain.ContainmentConflict]: archive covered, or split covering.
 */
@Singleton
class ResolveContainment @Inject constructor(
	private val entryRepository: EntryRepository,
	private val splitTimedEntry: SplitTimedEntry,
) {
	suspend fun apply(
		choice: ContainmentChoice,
		conflict: ContainmentConflict,
		coveringEntry: Entry,
		coveredEntry: Entry?,
	): ContainmentResolveResult {
		return when (choice) {
			ContainmentChoice.DELETE_COVERED -> {
				val id = conflict.covered.entryId
				val target = coveredEntry ?: entryRepository.getById(id)
					?: error("covered Entry $id missing")
				entryRepository.save(
					target.copy(archivedAtEpochMs = System.currentTimeMillis()),
				)
				ContainmentResolveResult.DeletedCovered(id)
			}
			ContainmentChoice.SPLIT_COVERING -> {
				val split = splitTimedEntry.split(
					coveringEntry,
					conflict.covered.start,
					conflict.covered.end,
				)
				val primaryId = entryRepository.save(split.primary)
				val secondaryId = split.secondary?.let { entryRepository.save(it) }
				ContainmentResolveResult.SplitApplied(primaryId, secondaryId)
			}
		}
	}
}
