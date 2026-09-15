package app.lade.agenda.data.overlap

import app.lade.agenda.api.entry.EntryModel
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlapSplitter @Inject constructor() {
    fun split(
        covering: EntryModel,
        cutStart: LocalTime,
        cutEnd: LocalTime,
    ): OverlapSplitterModel {
        val start = covering.startTime ?: error("covering Entry needs startTime")
        val end = covering.endTime ?: error("covering Entry needs endTime")
        val hasLeft = cutStart > start
        val hasRight = cutEnd < end
        return when {
            hasLeft && hasRight -> OverlapSplitterModel(
                primary = covering.copy(endTime = cutStart),
                secondary = covering.copy(
                    id = 0,
                    startTime = cutEnd,
                    endTime = end,
                    createdAtEpochMs = System.currentTimeMillis(),
                ),
            )
            hasLeft -> OverlapSplitterModel(covering.copy(endTime = cutStart), null)
            else -> OverlapSplitterModel(covering.copy(startTime = cutEnd), null)
        }
    }
}