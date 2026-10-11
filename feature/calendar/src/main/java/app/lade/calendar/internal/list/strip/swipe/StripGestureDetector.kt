package app.lade.calendar.internal.list.strip.swipe

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import app.lade.calendar.internal.list.strip.port.StripGesturePort
import kotlin.math.abs

internal fun Modifier.stripGesture(
    port: StripGesturePort,
    thresholdPx: Float,
    fullScrollPx: Float,
    stripDate: java.time.LocalDate,
    onTap: (Offset) -> Unit,
): Modifier = pointerInput(port, thresholdPx, fullScrollPx, stripDate) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val tracker = VelocityTracker()
        var axisLocked = false
        var isVertical = false
        var totalX = 0f
        var totalY = 0f

        tracker.addPosition(down.uptimeMillis, down.position)

        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break

            val delta = change.positionChange()
            val dx = delta.x
            val dy = delta.y

            tracker.addPosition(change.uptimeMillis, change.position)

            if (!axisLocked) {
                totalX += dx
                totalY += dy
                if (abs(totalX) > thresholdPx || abs(totalY) > thresholdPx) {
                    isVertical = abs(totalY) > abs(totalX)
                    axisLocked = true
                }
            } else {
                if (isVertical) {
                    change.consume()
                    port.onProgressDrag(dy, fullScrollPx)
                } else {
                    change.consume()
                    port.onDrag(dx)
                }
            }

            if (!change.pressed) break
        }

        if (!axisLocked) {
            onTap(down.position)
            return@awaitEachGesture
        }

        val velocity = tracker.calculateVelocity()
        if (isVertical) {
            port.onProgressRelease(velocity.y)
        } else {
            port.onRelease(velocity.x)
        }
    }
}