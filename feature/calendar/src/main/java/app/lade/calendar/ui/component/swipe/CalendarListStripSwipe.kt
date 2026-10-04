package app.lade.calendar.ui.component.swipe

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarListStripMode
import kotlin.math.abs

@Composable
fun CalendarListStripSwipe(
    mode: CalendarListStripMode,
    stripHeightPx: Float,
    onVerticalDrag: (dy: Float) -> Unit,
    onVerticalEnd: (totalY: Float, mode: CalendarListStripMode) -> Unit,
    onHorizontal: (CalendarDateMode) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val thresholdPx = with(LocalDensity.current) { 8.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(mode, stripHeightPx, thresholdPx) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown(
                            requireUnconsumed = false,
                            pass = PointerEventPass.Initial,
                        )
                        val downY = down.position.y
                        val inStrip = downY < stripHeightPx

                        var totalY = 0f
                        var totalX = 0f
                        var axisLocked = false
                        var isVertical = false

                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            val dy = change.position.y - change.previousPosition.y
                            val dx = change.position.x - change.previousPosition.x

                            if (!axisLocked) {
                                totalY += dy
                                totalX += dx
                                if (abs(totalY) > 8f || abs(totalX) > 8f) {
                                    isVertical = abs(totalY) > abs(totalX)
                                    axisLocked = true
                                }
                            } else if (isVertical) {
                                change.consume()
                                totalY += dy
                                onVerticalDrag(dy)
                            } else if (inStrip) {
                                change.consume()
                                totalX += dx
                            }

                            if (!change.pressed) break
                        }

                        if (axisLocked) {
                            if (isVertical) {
                                onVerticalEnd(totalY, mode)
                            } else if (inStrip) {
                                if (abs(totalX) > thresholdPx) {
                                    if (totalX > 0f) onHorizontal(CalendarDateMode.BACKWARD)
                                    else onHorizontal(CalendarDateMode.FORWARD)
                                }
                            }
                        }
                    }
                }
            },
    ) {
        content()
    }
}