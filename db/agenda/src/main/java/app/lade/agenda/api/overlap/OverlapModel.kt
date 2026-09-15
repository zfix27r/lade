package app.lade.agenda.api.overlap

data class OverlapModel(
    val covering: OverlapIntervalModel,
    val covered: OverlapIntervalModel,
)