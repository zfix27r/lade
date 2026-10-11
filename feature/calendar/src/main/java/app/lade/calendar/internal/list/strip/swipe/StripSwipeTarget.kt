package app.lade.calendar.internal.list.strip.swipe

internal fun stripSwipeTarget(
    offsetX: Float,
    totalX: Float,
    thresholdPx: Float,
    widthPx: Float,
    releaseFraction: Float,
): Float = when {
    totalX > thresholdPx -> widthPx
    totalX < -thresholdPx -> -widthPx
    offsetX > widthPx * releaseFraction -> widthPx
    offsetX < -widthPx * releaseFraction -> -widthPx
    else -> 0f
}