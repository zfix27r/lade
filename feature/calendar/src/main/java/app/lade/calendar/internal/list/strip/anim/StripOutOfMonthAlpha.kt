package app.lade.calendar.internal.list.strip.anim

internal fun outOfMonthAlpha(
    progress: Float,
    start: Float,
): Float {
    if (progress <= start) return 0f
    if (progress >= 1f) return 1f
    return (progress - start) / (1f - start)
}