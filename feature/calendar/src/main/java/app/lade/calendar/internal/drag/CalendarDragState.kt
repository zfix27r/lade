package app.lade.calendar.internal.drag

/**
 * Снапшот состояния жеста. Источник правды — [CalendarDragStateHolder.state].
 *
 * Знак направления не хранится отдельно — он в знаке [totalOffset]:
 *  - `> 0` — вправо (Horizontal) или вниз (Vertical);
 *  - `< 0` — влево (Horizontal) или вверх (Vertical).
 */
internal data class DragState(
    val phase: CalendarDragPhase = CalendarDragPhase.IDLE,
    val direction: CalendarDragDirection = CalendarDragDirection.NONE,
    val totalOffset: Float = 0f,
    val velocity: Float = 0f,
)