package app.lade.calendar.internal.list.strip.port

import androidx.compose.runtime.State
import app.lade.calendar.internal.list.strip.data.StripStateHolder
import app.lade.calendar.internal.list.strip.data.stripShiftDate
import app.lade.calendar.internal.list.strip.swipe.StripSwipeGesture
import app.lade.calendar.internal.list.strip.swipe.StripSwipeState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate

internal class StripPortImpl(
    private val holder: StripStateHolder,
    private val gesture: StripSwipeGesture,
    override val portModel: StripPortModel,
) : StripViewPort, StripGesturePort, StripScrollPort, StripOutsidePort {

    private val _onDateSelected = MutableSharedFlow<LocalDate>(extraBufferCapacity = 8)
    override val dateSelected: SharedFlow<LocalDate> = _onDateSelected.asSharedFlow()

    override val swipeState: State<StripSwipeState> get() = holder.swipeState
    override val stripDate: State<LocalDate> get() = holder.stripDate

    override val widthPx: State<Int> get() = holder.widthPx
    override val targetOffsetX: State<Float> get() = holder.targetOffsetX
    override val targetProgress: State<Float> get() = holder.targetProgress
    override val isDragging: State<Boolean> get() = holder.isDragging

    override val lastVelocityX: Float get() = gesture.lastVelocityX
    override val progress: State<Float> get() = holder.targetProgress

    override fun onDateSelected(date: LocalDate) {
        _onDateSelected.tryEmit(date)
    }

    override fun onWidthChanged(widthPx: Int) = holder.setWidthPx(widthPx)

    override fun onSettleComplete() {
        val direction = when {
            holder.targetOffsetX.floatValue > 0f -> -1
            holder.targetOffsetX.floatValue < 0f -> 1
            else -> return
        }
        val isMonthMode = holder.targetProgress.floatValue >= 0.5f
        val newDate = stripShiftDate(
            date = holder.stripDate.value,
            steps = direction,
            isMonthMode = isMonthMode,
        )
        holder.setStripDate(newDate)
        holder.commitPageShift()
    }

    override fun onDrag(deltaX: Float) = gesture.onDrag(deltaX)
    override fun onRelease(velocityX: Float) = gesture.onRelease(velocityX)
    override fun onProgressDrag(deltaY: Float, fullScrollPx: Float) =
        gesture.onProgressDrag(deltaY, fullScrollPx)
    override fun onProgressRelease(velocityY: Float) =
        gesture.onProgressRelease(velocityY)

    override fun onScroll(deltaY: Float, fullScrollPx: Float): Float {
        gesture.onProgressDrag(deltaY, fullScrollPx)
        return deltaY
    }

    override fun onStop() {
        gesture.onProgressRelease(0f)
    }
}