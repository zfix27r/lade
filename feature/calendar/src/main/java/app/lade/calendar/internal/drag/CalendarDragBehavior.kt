package app.lade.calendar.internal.drag

internal data class CalendarDragBehavior(
    val enabled: Boolean,
    val mode: CalendarDragMode,
    val start: Float,
    val end: Float,
    val hasBounds: Boolean,
    val initial: Float,
) {
    val isBounded: Boolean get() = hasBounds && start < end

    fun clamp(value: Float): Float =
        if (isBounded) value.coerceIn(start, end) else value

    companion object {
        fun scroll(
            start: Float,
            end: Float,
            initial: Float = start,
            enabled: Boolean = true,
            hasBounds: Boolean = true,
        ): CalendarDragBehavior = CalendarDragBehavior(
            enabled = enabled,
            mode = CalendarDragMode.SCROLL,
            start = start,
            end = end,
            hasBounds = hasBounds,
            initial = initial,
        )

        fun pages(
            initial: Float = 0f,
            enabled: Boolean = true,
            hasBounds: Boolean = false,
            start: Float = 0f,
            end: Float = 0f,
        ): CalendarDragBehavior = CalendarDragBehavior(
            enabled = enabled,
            mode = CalendarDragMode.PAGES,
            start = start,
            end = end,
            hasBounds = hasBounds,
            initial = initial,
        )

        val disabled: CalendarDragBehavior = CalendarDragBehavior(
            enabled = false,
            mode = CalendarDragMode.PAGES,
            start = 0f,
            end = 0f,
            hasBounds = false,
            initial = 0f,
        )
    }
}