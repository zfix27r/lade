package app.lade.calendar.internal.list.strip.swipe

import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.list.strip.data.StripStateHolder
import kotlin.math.abs

internal class StripSwipeGesture(
    private val holder: StripStateHolder,
    private val config: StripConfig,
) {

    var lastVelocityX: Float = 0f
        private set

    fun onDrag(delta: Float) {
        val width = holder.widthPx.intValue.toFloat()
        if (width <= 0f) return
        holder.setDragging(true)
        val current = holder.targetOffsetX.floatValue
        val next = (current + delta).coerceIn(-width, width)
        holder.setTargetOffsetX(next)
        holder.setSwipeState(classifySwipe(next, width))
    }

    fun onRelease(velocityX: Float) {
        val width = holder.widthPx.intValue.toFloat()
        if (width <= 0f) return

        lastVelocityX = velocityX
        holder.setDragging(false)

        val current = holder.targetOffsetX.floatValue
        val threshold = width * config.releaseThreshold
        val fling = abs(velocityX) > config.swipeFlingVelocityThreshold

        val goBackward = when {
            fling -> velocityX > 0f
            abs(current) >= threshold -> current > 0f
            else -> false
        }
        val goForward = when {
            fling -> velocityX < 0f
            abs(current) >= threshold -> current < 0f
            else -> false
        }

        when {
            goBackward -> holder.setTargetOffsetX(width)
            goForward -> holder.setTargetOffsetX(-width)
            else -> holder.setTargetOffsetX(0f)
        }
        holder.setSwipeState(StripSwipeState.NONE)
    }

    fun onProgressDrag(delta: Float, fullScrollPx: Float) {
        if (fullScrollPx <= 0f) return
        holder.setDragging(true)
        val current = holder.targetProgress.floatValue
        val next = (current + delta / fullScrollPx).coerceIn(0f, 1f)
        holder.setTargetProgress(next)
    }

    fun onProgressRelease(velocityY: Float) {
        holder.setDragging(false)
        val threshold = config.swipeVerticalReleaseThreshold
        val fling = abs(velocityY) > config.swipeVerticalFlingVelocityThreshold
        val current = holder.targetProgress.floatValue

        val target = when {
            fling -> if (velocityY < 0f) 1f else 0f
            current >= threshold -> 1f
            else -> 0f
        }
        holder.setTargetProgress(target)
    }

    private fun classifySwipe(offsetX: Float, width: Float): StripSwipeState {
        if (offsetX == 0f) return StripSwipeState.NONE
        val threshold = width * config.releaseThreshold
        return when {
            offsetX > 0f ->
                if (offsetX >= threshold) StripSwipeState.BACKWARD_FINAL
                else StripSwipeState.BACKWARD
            else ->
                if (-offsetX >= threshold) StripSwipeState.FORWARD_FINAL
                else StripSwipeState.FORWARD
        }
    }
}