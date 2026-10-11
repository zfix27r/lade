package app.lade.calendar.internal.list.strip.swipe

internal fun stripOutOfMonthAlpha(
    progress: Float,
    threshold: Float,
): Float {
    if (progress <= 0f) return 1f
    if (progress >= threshold) return 0f
    return 1f - progress / threshold
}