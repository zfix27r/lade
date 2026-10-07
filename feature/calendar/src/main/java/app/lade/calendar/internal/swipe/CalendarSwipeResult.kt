package app.lade.calendar.internal.swipe

internal sealed interface CalendarSwipeResult {
    data class Horizontal(val totalX: Float) : CalendarSwipeResult
    data class Vertical(val totalY: Float) : CalendarSwipeResult
    data class Tap(val x: Float, val y: Float) : CalendarSwipeResult
}