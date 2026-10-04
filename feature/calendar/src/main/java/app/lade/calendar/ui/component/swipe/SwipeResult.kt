package app.lade.calendar.ui.component.swipe

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.abs

sealed interface SwipeResult {
    data class Horizontal(val totalX: Float) : SwipeResult
    data class Vertical(val totalY: Float) : SwipeResult
    data object Tap : SwipeResult
}

suspend fun AwaitPointerEventScope.awaitSwipe(
    thresholdPx: Float,
    pass: PointerEventPass = PointerEventPass.Main,
    onDrag: (isVertical: Boolean, delta: Float) -> Unit = { _, _ -> },
): SwipeResult {
    val down = awaitFirstDown(requireUnconsumed = false, pass = pass)
    var totalY = 0f
    var totalX = 0f
    var axisLocked = false
    var isVertical = false

    while (true) {
        val event = awaitPointerEvent(pass)
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
            change.consume()
            if (isVertical) {
                totalY += dy
                onDrag(true, dy)
            } else {
                totalX += dx
                onDrag(false, dx)
            }
        }

        if (change.changedToUp()) break
    }

    return when {
        !axisLocked -> SwipeResult.Tap
        isVertical -> SwipeResult.Vertical(totalY)
        else -> SwipeResult.Horizontal(totalX)
    }
}

fun Modifier.calendarSwipe(
    thresholdPx: Float,
    pass: PointerEventPass = PointerEventPass.Main,
    onDrag: (isVertical: Boolean, delta: Float) -> Unit = { _, _ -> },
    onResult: (SwipeResult) -> Unit,
): Modifier = composed {
    pointerInput(thresholdPx, pass) {
        awaitPointerEventScope {
            while (true) {
                val result = awaitSwipe(thresholdPx, pass, onDrag)
                onResult(result)
            }
        }
    }
}