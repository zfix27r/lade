package app.lade.calendar.internal.drag

import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class CalendarDragStateHolder(
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(DragState())
    val state: StateFlow<DragState> = _state.asStateFlow()

    private val _settleEvents = MutableSharedFlow<Float>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val settleEvents: SharedFlow<Float> = _settleEvents.asSharedFlow()

    @Volatile
    private var phase: CalendarDragPhase = CalendarDragPhase.IDLE

    @Volatile
    private var direction: CalendarDragDirection = CalendarDragDirection.NONE

    @Volatile
    private var totalOffset: Float = 0f

    @Volatile
    private var velocity: Float = 0f

    internal val currentOffset: Float get() = totalOffset
    internal val isSettling: Boolean get() = phase == CalendarDragPhase.SETTLED

    private val pump: Job = scope.launch {
        while (true) {
            withFrameNanos { }
            _state.value = DragState(
                phase = phase,
                direction = direction,
                totalOffset = totalOffset,
                velocity = velocity,
            )
        }
    }

    internal fun reset() {
        phase = CalendarDragPhase.PRESSED
        direction = CalendarDragDirection.NONE
        totalOffset = 0f        // ← единственное изменение
        velocity = 0f
    }

    internal fun lock(direction: CalendarDragDirection) {
        this.phase = CalendarDragPhase.DRAGGING
        this.direction = direction
    }

    internal fun drag(totalOffset: Float) {
        this.totalOffset = totalOffset
    }

    internal fun settle(velocity: Float) {
        this.phase = CalendarDragPhase.SETTLED
        this.velocity = velocity
        _settleEvents.tryEmit(velocity)
    }

    internal fun cancel() {
        this.phase = CalendarDragPhase.IDLE
        this.direction = CalendarDragDirection.NONE
        this.totalOffset = 0f
        this.velocity = 0f
    }

    internal fun dispose() {
        pump.cancel()
    }
}