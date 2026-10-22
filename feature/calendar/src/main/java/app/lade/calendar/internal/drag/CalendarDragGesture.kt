package app.lade.calendar.internal.drag

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker

internal fun Modifier.dragGesture(
    holder: CalendarDragStateHolder,
    config: CalendarDragGestureConfig,
    onTap: ((Offset) -> Unit)? = null,
): Modifier = composed {
    val currentOnTap by rememberUpdatedState(onTap)
    val currentConfig by rememberUpdatedState(config)

    this.pointerInput(holder) {
        awaitEachGesture {
            val cfg = currentConfig
            val down = awaitFirstDown(requireUnconsumed = false, pass = cfg.eventPass)
            val downPosition = down.position
            val velocityTracker = VelocityTracker()
            velocityTracker.addPosition(down.uptimeMillis, downPosition)

            val gesture = CalendarDragGestureTracker(
                horizontal = holder.horizontal,
                vertical = holder.vertical,
                thresholdPx = cfg.axisLockThresholdPx,
            )
            val wasSettling = holder.isSettling
            holder.reset()
            gesture.begin(downPosition.x, downPosition.y, down.uptimeMillis)

            var upTime = down.uptimeMillis

            while (true) {
                val event = awaitPointerEvent(cfg.eventPass)
                val change = event.changes.firstOrNull { it.id == down.id }
                if (change == null) {
                    holder.cancel()
                    return@awaitEachGesture
                }

                val delta = gesture.moveTo(change.position.x, change.position.y)
                gesture.accumulate(delta)

                if (!gesture.isLocked) {
                    gesture.lockIfNeeded(holder)
                }

                if (gesture.isLocked) {
                    holder.drag(gesture.currentOffset())
                    if (gesture.shouldConsume(cfg.consumeHorizontal, cfg.consumeVertical)) {
                        change.consume()
                    }
                }

                velocityTracker.addPosition(change.uptimeMillis, change.position)
                upTime = change.uptimeMillis

                if (!change.pressed) break
            }

            val velocityVector = velocityTracker.calculateVelocity()
            val velocity = if (gesture.direction == CalendarDragDirection.VERTICAL) {
                velocityVector.y
            } else {
                velocityVector.x
            }

            when (val result = gesture.finish(upTime, wasSettling, cfg.tapTimeoutMs, velocity)) {
                is CalendarDragResult.Settle -> holder.settle(result.velocity)
                CalendarDragResult.Tap -> {
                    holder.cancel()
                    currentOnTap?.invoke(downPosition)
                }
                CalendarDragResult.Cancel -> holder.cancel()
            }
        }
    }
}