package app.lade.agenda.api.overlap

data class Overlap(
    val covering: OverlapInterval,
    val covered: OverlapInterval,
)