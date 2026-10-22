package app.lade.calendar.internal.drag

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class CalendarDragStateHolder {
    private val _state = MutableStateFlow(
        CalendarDragState(
            phase = CalendarDragPhase.IDLE,
            direction = CalendarDragDirection.NONE,
            offset = 0f,
            velocity = 0f,
        ),
    )
    val state: StateFlow<CalendarDragState> = _state.asStateFlow()

    @Volatile
    var horizontal: CalendarDragBehavior = CalendarDragBehavior.disabled
        private set

    @Volatile
    var vertical: CalendarDragBehavior = CalendarDragBehavior.disabled
        private set

    @Volatile private var phase: CalendarDragPhase = CalendarDragPhase.IDLE
    @Volatile private var direction: CalendarDragDirection = CalendarDragDirection.NONE
    @Volatile private var offset: Float = 0f
    @Volatile private var velocity: Float = 0f

    @Volatile private var settledX: Float? = null
    @Volatile private var settledY: Float? = null

    internal val isSettling: Boolean get() = phase == CalendarDragPhase.SETTLED

    internal fun updateAxes(horizontal: CalendarDragBehavior, vertical: CalendarDragBehavior) {
        val previousHorizontalInitial = this.horizontal.initial
        val previousVerticalInitial = this.vertical.initial

        this.horizontal = horizontal
        this.vertical = vertical

        if (horizontal.mode == CalendarDragMode.PAGES) {
            settledX = null
        } else if (settledX == null || settledX == previousHorizontalInitial) {
            settledX = horizontal.initial
        }

        if (vertical.mode == CalendarDragMode.PAGES) {
            settledY = null
        } else if (settledY == null || settledY == previousVerticalInitial) {
            settledY = vertical.initial
        }

        if (phase == CalendarDragPhase.IDLE) {
            direction = when {
                settledY != null && vertical.enabled -> CalendarDragDirection.VERTICAL
                settledX != null && horizontal.enabled -> CalendarDragDirection.HORIZONTAL
                else -> CalendarDragDirection.NONE
            }
            offset = when (direction) {
                CalendarDragDirection.HORIZONTAL -> settledX ?: horizontal.initial
                CalendarDragDirection.VERTICAL -> settledY ?: vertical.initial
                CalendarDragDirection.NONE -> 0f
            }
        }

        publish()
    }

    internal fun reset() {
        phase = CalendarDragPhase.PRESSED
        velocity = 0f

        val keepHorizontal = horizontal.mode == CalendarDragMode.SCROLL && settledX != null
        val keepVertical = vertical.mode == CalendarDragMode.SCROLL && settledY != null

        direction = when {
            keepHorizontal && !keepVertical -> CalendarDragDirection.HORIZONTAL
            keepVertical && !keepHorizontal -> CalendarDragDirection.VERTICAL
            else -> CalendarDragDirection.NONE
        }
        offset = when (direction) {
            CalendarDragDirection.HORIZONTAL -> settledX ?: horizontal.initial
            CalendarDragDirection.VERTICAL -> settledY ?: vertical.initial
            CalendarDragDirection.NONE -> 0f
        }
        publish()
    }

    internal fun lock(direction: CalendarDragDirection) {
        phase = CalendarDragPhase.DRAGGING
        this.direction = direction
        offset = when (direction) {
            CalendarDragDirection.HORIZONTAL -> settledX ?: horizontal.initial
            CalendarDragDirection.VERTICAL -> settledY ?: vertical.initial
            CalendarDragDirection.NONE -> 0f
        }
        publish()
    }

    internal fun drag(value: Float) {
        if (offset == value) return
        offset = value
        publish()
    }

    internal fun settle(velocity: Float) {
        commitCurrentOffset()
        phase = CalendarDragPhase.IDLE
        this.velocity = 0f
        publish()
    }

    internal fun cancel() {
        commitCurrentOffset()
        phase = CalendarDragPhase.IDLE
        velocity = 0f
        publish()
    }

    private fun commitCurrentOffset() {
        when (direction) {
            CalendarDragDirection.HORIZONTAL -> {
                if (horizontal.mode == CalendarDragMode.SCROLL) {
                    settledX = offset
                } else {
                    direction = CalendarDragDirection.NONE
                    offset = 0f
                }
            }
            CalendarDragDirection.VERTICAL -> {
                if (vertical.mode == CalendarDragMode.SCROLL) {
                    settledY = offset
                } else {
                    direction = CalendarDragDirection.NONE
                    offset = 0f
                }
            }
            CalendarDragDirection.NONE -> {
                offset = 0f
            }
        }
    }

    private fun publish() {
        _state.value = CalendarDragState(
            phase = phase,
            direction = direction,
            offset = offset,
            velocity = velocity,
        )
    }
}