package app.lade.entry.domain

import app.lade.entry.data.IntervalContainment
import app.lade.entry.domain.models.Entry
import app.lade.entry.domain.models.SplitResult
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplitTimedEntry @Inject constructor() {
	fun split(covering: Entry, cutStart: LocalTime, cutEnd: LocalTime): SplitResult {
		val start = covering.startTime
			?: error("covering Entry needs startTime")
		val end = covering.endTime
			?: error("covering Entry needs endTime")
		require(end > start) { "covering interval invalid" }
		require(IntervalContainment.fullyContains(start, end, cutStart, cutEnd)) {
			"cut must be fully inside covering"
		}
		val hasLeft = cutStart > start
		val hasRight = cutEnd < end
		require(hasLeft || hasRight) {
			"cut equals covering — choose delete covered instead"
		}
		return when {
			hasLeft && hasRight -> SplitResult(
				primary = covering.copy(endTime = cutStart),
				secondary = covering.copy(
					id = 0,
					startTime = cutEnd,
					endTime = end,
					createdAtEpochMs = System.currentTimeMillis(),
				),
			)
			hasLeft -> SplitResult(
				primary = covering.copy(endTime = cutStart),
				secondary = null,
			)
			else -> SplitResult(
				primary = covering.copy(startTime = cutEnd),
				secondary = null,
			)
		}
	}
}
