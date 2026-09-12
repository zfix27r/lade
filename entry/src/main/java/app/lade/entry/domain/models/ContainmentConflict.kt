package app.lade.entry.domain.models

data class ContainmentConflict(
    val covering: TimedIntervalRef,
    val covered: TimedIntervalRef,
)
