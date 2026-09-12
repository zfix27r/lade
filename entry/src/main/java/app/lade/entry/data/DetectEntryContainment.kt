package app.lade.entry.data

import app.lade.entry.domain.DayPlan
import app.lade.entry.domain.models.ContainmentConflict
import app.lade.entry.domain.models.TimedIntervalRef
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetectEntryContainment @Inject constructor() {
	fun findConflicts(
		candidateId: Long,
		candidateStart: LocalTime,
		candidateEnd: LocalTime,
		existing: List<TimedIntervalRef>,
	): List<ContainmentConflict> {
		if (candidateEnd <= candidateStart) return emptyList()
		val candidate = TimedIntervalRef(
			entryId = candidateId,
			start = candidateStart,
			end = candidateEnd,
		)
		val others = existing.filter { it.entryId != candidateId && it.end > it.start }
		val out = ArrayList<ContainmentConflict>()
		for (other in others) {
			when {
				IntervalContainment.fullyContains(
					candidate.start, candidate.end, other.start, other.end,
				) -> out += ContainmentConflict(covering = candidate, covered = other)
				IntervalContainment.fullyContains(
					other.start, other.end, candidate.start, candidate.end,
				) -> out += ContainmentConflict(covering = other, covered = candidate)
			}
		}
		return out
	}

	fun findConflictsAgainstDayPlan(
		candidateId: Long,
		candidateStart: LocalTime,
		candidateEnd: LocalTime,
		plan: DayPlan,
	): List<ContainmentConflict> {
		val existing = plan.timed
			.filter { it.hasTime }
			.map {
				TimedIntervalRef(
					entryId = it.entryId,
					start = it.start!!,
					end = it.end!!,
					title = it.title,
					fromSeries = it.fromSeries,
				)
			}
		return findConflicts(candidateId, candidateStart, candidateEnd, existing)
	}
}