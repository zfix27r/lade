package app.lade.calendar.ui.component.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarListStripMode
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CalendarStripSwipe(
    stripMode: CalendarListStripMode,
    onHorizontal: (CalendarDateMode) -> Unit,
    onVertical: (CalendarListStripMode) -> Unit,
    modifier: Modifier = Modifier,
    pageContent: @Composable (pageOffset: Int) -> Unit,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var widthPx by remember { mutableIntStateOf(0) }

    val horizontalThresholdPx = with(density) { 48.dp.toPx() }
    val verticalThresholdPx = with(density) { 48.dp.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { widthPx = it.width }
            .pointerInput(stripMode, widthPx) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown(
                            requireUnconsumed = false,
                            pass = PointerEventPass.Initial,
                        )
                        var totalX = 0f
                        var totalY = 0f
                        var axisLocked = false
                        var isHorizontal = false

                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break

                            val dx = change.position.x - change.previousPosition.x
                            val dy = change.position.y - change.previousPosition.y

                            if (!axisLocked) {
                                totalX += dx
                                totalY += dy
                                val ax = abs(totalX)
                                val ay = abs(totalY)
                                if (ax > 12f || ay > 12f) {
                                    isHorizontal = ax > ay
                                    axisLocked = true
                                }
                            } else {
                                if (isHorizontal) {
                                    totalX += dx
                                    change.consume()
                                    scope.launch { offsetX.snapTo(totalX) }
                                } else {
                                    totalY += dy
                                    change.consume()
                                }
                            }

                            if (!change.pressed) break
                        }

                        if (axisLocked) {
                            if (isHorizontal) {
                                val width = widthPx.toFloat()
                                if (abs(totalX) > horizontalThresholdPx) {
                                    if (totalX > 0f) {
                                        scope.launch {
                                            offsetX.animateTo(width, tween(200))
                                            onHorizontal(CalendarDateMode.BACKWARD)
                                            offsetX.snapTo(0f)
                                        }
                                    } else {
                                        scope.launch {
                                            offsetX.animateTo(-width, tween(200))
                                            onHorizontal(CalendarDateMode.FORWARD)
                                            offsetX.snapTo(0f)
                                        }
                                    }
                                } else {
                                    scope.launch { offsetX.animateTo(0f, tween(200)) }
                                }
                            } else {
                                if (abs(totalY) > verticalThresholdPx) {
                                    when {
                                        totalY > 0f && stripMode == CalendarListStripMode.MONTH ->
                                            onVertical(CalendarListStripMode.WEEK)
                                        totalY < 0f && stripMode == CalendarListStripMode.WEEK ->
                                            onVertical(CalendarListStripMode.MONTH)
                                    }
                                }
                                scope.launch { offsetX.snapTo(0f) }
                            }
                        } else {
                            scope.launch { offsetX.snapTo(0f) }
                        }
                    }
                }
            },
    ) {
        val offsetValue by offsetX.asState()
        val width = constraints.maxWidth.toFloat()

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset((offsetValue - width).roundToInt(), 0) },
            ) {
                pageContent(-1)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetValue.roundToInt(), 0) },
            ) {
                pageContent(0)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset((offsetValue + width).roundToInt(), 0) },
            ) {
                pageContent(1)
            }
        }
    }
}