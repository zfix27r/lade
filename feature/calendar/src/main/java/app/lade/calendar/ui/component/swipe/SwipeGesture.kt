package app.lade.calendar.ui.component.swipe

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.Modifier
import kotlin.math.abs

sealed interface SwipeResult {
    data class Horizontal(val totalX: Float) : SwipeResult
    data class Vertical(val totalY: Float) : SwipeResult
    data class Tap(val x: Float, val y: Float) : SwipeResult
}

fun Modifier.calendarSwipe(
    thresholdPx: Float,
    consumeVertical: Boolean = true,
    consumeHorizontal: Boolean = true,
    onDrag: (isVertical: Boolean, delta: Float) -> Unit = { _, _ -> },
    onResult: (SwipeResult) -> Unit,
): Modifier = pointerInput(thresholdPx) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val downX = down.position.x
        val downY = down.position.y
        var totalY = 0f
        var totalX = 0f
        var axisLocked = false
        var isVertical = false

        do {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break

            val delta = change.positionChange()
            val dy = delta.y
            val dx = delta.x

            if (!axisLocked) {
                totalY += dy
                totalX += dx
                if (abs(totalY) > thresholdPx || abs(totalX) > thresholdPx) {
                    isVertical = abs(totalY) > abs(totalX)
                    axisLocked = true
                }
            } else {
                if (isVertical) {
                    if (consumeVertical) change.consume()
                    totalY += dy
                    onDrag(true, dy)
                } else {
                    if (consumeHorizontal) change.consume()
                    totalX += dx
                    onDrag(false, dx)
                }
            }
        } while (event.changes.any { it.pressed })

        val result = when {
            !axisLocked -> SwipeResult.Tap(downX, downY)
            isVertical -> SwipeResult.Vertical(totalY)
            else -> SwipeResult.Horizontal(totalX)
        }
        onResult(result)
    }
}