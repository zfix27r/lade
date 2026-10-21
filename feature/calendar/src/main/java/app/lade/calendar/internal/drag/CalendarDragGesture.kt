package app.lade.calendar.internal.drag

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlin.math.abs

internal fun Modifier.dragGesture(
    holder: CalendarDragStateHolder,
    axisLockThresholdPx: Float,
    tapTimeoutMs: Long = 200L,
    consumeVertical: Boolean = true,
    consumeHorizontal: Boolean = true,
    eventPass: PointerEventPass = PointerEventPass.Main,
    onTap: ((Offset) -> Unit)? = null,
): Modifier = composed {
    val currentOnTap by rememberUpdatedState(onTap)
    val currentThreshold by rememberUpdatedState(axisLockThresholdPx)
    val currentTapTimeout by rememberUpdatedState(tapTimeoutMs)
    val currentConsumeVertical by rememberUpdatedState(consumeVertical)
    val currentConsumeHorizontal by rememberUpdatedState(consumeHorizontal)
    val currentEventPass by rememberUpdatedState(eventPass)

    this.pointerInput(holder) {
        awaitEachGesture {
            val pass = currentEventPass
            val down = awaitFirstDown(requireUnconsumed = false, pass = pass)
            val downPosition = down.position
            val downTime = down.uptimeMillis

            val wasSettling = holder.isSettling
            holder.reset()

            val tracker = VelocityTracker()
            tracker.addPosition(downTime, downPosition)

            var lastX = downPosition.x
            var lastY = downPosition.y
            var totalX = 0f
            var totalY = 0f
            var locked = false
            var isVertical = false
            var upTime = downTime

            while (true) {
                val event = awaitPointerEvent(pass)

                val change = event.changes.firstOrNull { it.id == down.id }
                if (change == null) {
                    holder.cancel()
                    return@awaitEachGesture
                }

                val dx = change.position.x - lastX
                val dy = change.position.y - lastY
                lastX = change.position.x
                lastY = change.position.y

                tracker.addPosition(change.uptimeMillis, change.position)

                if (!locked) {
                    totalX += dx
                    totalY += dy
                    if (abs(totalX) > currentThreshold || abs(totalY) > currentThreshold) {
                        isVertical = abs(totalY) > abs(totalX)
                        val dir =
                            if (isVertical) CalendarDragDirection.VERTICAL else CalendarDragDirection.HORIZONTAL
                        holder.lock(dir)
                        locked = true
                        holder.drag(if (isVertical) totalY else totalX)   // ← без startOffset
                    }
                } else {
                    if (isVertical) {
                        if (currentConsumeVertical) change.consume()
                        totalY += dy
                        holder.drag(totalY)                              // ← без startOffset
                    } else {
                        if (currentConsumeHorizontal) change.consume()
                        totalX += dx
                        holder.drag(totalX)                              // ← без startOffset
                    }
                }

                if (!change.pressed) {
                    upTime = change.uptimeMillis
                    break
                }
            }

            val duration = upTime - downTime

            if (locked) {
                val velocity = tracker.calculateVelocity()
                val v = if (isVertical) velocity.y else velocity.x
                holder.settle(v)
            } else if (wasSettling) {
                holder.settle(0f)
            } else if (duration <= currentTapTimeout) {
                holder.cancel()
                currentOnTap?.invoke(downPosition)
            } else {
                holder.cancel()
            }
        }
    }
}