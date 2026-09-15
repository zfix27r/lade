package app.lade.agenda.api.overlap

sealed class OverlapResult {
    data class DeletedCovered(val coveredId: Long) : OverlapResult()
    data class SplitApplied(val primaryId: Long, val secondaryId: Long?) : OverlapResult()
}