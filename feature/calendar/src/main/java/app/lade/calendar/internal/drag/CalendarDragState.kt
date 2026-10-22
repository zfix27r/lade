package app.lade.calendar.internal.drag

internal data class CalendarDragState(
    val phase: CalendarDragPhase,
    val direction: CalendarDragDirection,
    val offset: Float,
    val velocity: Float,
) {
    val isDraggingVertical: Boolean
        get() = phase == CalendarDragPhase.DRAGGING && direction == CalendarDragDirection.VERTICAL

    val isDraggingHorizontal: Boolean
        get() = phase == CalendarDragPhase.DRAGGING && direction == CalendarDragDirection.HORIZONTAL
}