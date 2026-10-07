package app.lade.calendar.internal.list.strip.anim

import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.list.strip.data.StripStateHolder
import kotlin.math.abs

internal class StripSwipeGesture(
    private val holder: StripStateHolder,
    private val animator: StripSwipeAnimator,
    private val config: StripConfig,
) {

    fun progress(): Float = holder.state.value.progress

    fun onDrag(delta: Float) {
        animator.cancelAll()
        val s = holder.state.value
        val width = s.widthPx.toFloat()
        if (width <= 0f) return
        holder.setOffsetX((s.offsetX + delta).coerceIn(-width, width))
    }

    fun onRelease() {
        val s = holder.state.value
        val width = s.widthPx.toFloat()
        if (width <= 0f) return
        val current = s.offsetX
        val threshold = width * config.releaseThreshold

        if (abs(current) < threshold) {
            animator.animateOffsetTo(0f)
        } else {
            val target = if (current > 0f) width else -width
            val direction = if (current > 0f) -1 else 1
            animator.animateOffsetTo(target) {
                holder.setDate(s.shift(s.date, direction))
                holder.setOffsetX(0f)
            }
        }
    }

    fun onProgressDrag(delta: Float, fullScrollPx: Float) {
        val s = holder.state.value
        val next = (s.progress + delta / fullScrollPx).coerceIn(0f, 1f)
        holder.setProgress(next)
    }

    fun onProgressRelease(target: Float) {
        animator.animateProgressTo(target)
    }
}