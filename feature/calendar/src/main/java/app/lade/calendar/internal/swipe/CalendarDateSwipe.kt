package app.lade.calendar.internal.swipe

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import app.lade.calendar.internal.domain.CalendarDateMode
import app.lade.resources.R
import kotlin.math.abs


@Composable
internal fun CalendarDateSwipe(
    onSwipe: (CalendarDateMode) -> Unit,
    modifier: Modifier = Modifier,
    axis: CalendarSwipeAxis = CalendarSwipeAxis.HORIZONTAL,
    content: @Composable () -> Unit,
) {
    val thresholdPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.calendar_swipe_threshold).toPx()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(axis, thresholdPx) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var totalX = 0f
                    var totalY = 0f
                    var horizontal = false
                    var vertical = false

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break

                        val dx = change.position.x - change.previousPosition.x
                        val dy = change.position.y - change.previousPosition.y

                        if (!horizontal && !vertical) {
                            val ax = abs(totalX + dx)
                            val ay = abs(totalY + dy)
                            if (ax > ay && ax > 8f) horizontal = true
                            else if (ay >= ax && ay > 8f) vertical = true
                        }

                        if (axis == CalendarSwipeAxis.HORIZONTAL && horizontal) {
                            totalX += dx
                            change.consume()
                        } else if (axis == CalendarSwipeAxis.VERTICAL && vertical) {
                            totalY += dy
                            change.consume()
                        }

                        if (!change.pressed) break
                    }

                    val total = if (axis == CalendarSwipeAxis.HORIZONTAL) totalX else totalY
                    if (abs(total) > thresholdPx) {
                        if (total > 0f) onSwipe(CalendarDateMode.BACKWARD)
                        else onSwipe(CalendarDateMode.FORWARD)
                    }
                }
            },
    ) {
        content()
    }
}