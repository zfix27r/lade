package app.lade.agenda.api

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.overlap.Overlap
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapResult
import java.time.LocalDate
import java.time.LocalTime

interface OverlapResolver {
    suspend fun findOverlaps(
        entryId: Long,
        start: LocalTime,
        end: LocalTime,
        date: LocalDate,
    ): List<Overlap>

    suspend fun resolveOverlap(
        choice: OverlapChoice,
        overlap: Overlap,
        coveringEntry: EntryModel,
    ): OverlapResult
}