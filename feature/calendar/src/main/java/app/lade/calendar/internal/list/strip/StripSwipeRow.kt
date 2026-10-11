package app.lade.calendar.internal.list.strip

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.list.strip.data.stripShiftDate
import app.lade.calendar.internal.list.strip.data.stripWeeks
import app.lade.calendar.internal.list.strip.layout.StripLayout
import app.lade.calendar.internal.list.strip.layout.stripDateAt
import app.lade.calendar.internal.list.strip.layout.stripLayoutRemember
import app.lade.calendar.internal.list.strip.port.StripGesturePort
import app.lade.calendar.internal.list.strip.port.StripViewPort
import app.lade.calendar.internal.list.strip.swipe.stripGesture
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.temporal.WeekFields
import kotlin.math.abs

@Composable
private fun rememberAnimatableState(animatable: Animatable<Float, *>): State<Float> =
    remember(animatable) {
        object : State<Float> {
            override val value: Float get() = animatable.value
        }
    }

private fun settleDuration(
    distance: Float,
    velocity: Float,
): Int {
    val v = abs(velocity).coerceAtLeast(100f)
    val raw = distance / v * 1000f
    return raw.coerceIn(100f, 400f).toInt()
}

@Composable
internal fun StripSwipeRow(
    viewPort: StripViewPort,
    gesturePort: StripGesturePort,
    config: StripConfig,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }
    val thresholdPx = with(density) { 8.dp.toPx() }
    val fullScrollPx = with(density) {
        (config.rowHeight * config.scrollRows).toPx()
    }
    var measuredWidthPx by remember { mutableIntStateOf(0) }

    val stripDate = viewPort.stripDate.value
    val calendarDate = viewPort.portModel.calendarDate.value
    val markedDates = viewPort.portModel.calendarMarkedDates.value

    val offsetX = remember { Animatable(0f) }
    val progress = remember { Animatable(1f) }

    val offsetXState = rememberAnimatableState(offsetX)
    val progressState = rememberAnimatableState(progress)

    LaunchedEffect(viewPort) {
        snapshotFlow { viewPort.targetOffsetX.value to viewPort.isDragging.value }
            .distinctUntilChanged()
            .collect { (target, dragging) ->
                if (dragging) {
                    offsetX.snapTo(target)
                } else {
                    val start = offsetX.value
                    val distance = abs(target - start)
                    val duration = settleDuration(distance, gesturePort.lastVelocityX)
                    offsetX.animateTo(
                        targetValue = target,
                        animationSpec = tween(
                            durationMillis = duration,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                    if (target != 0f) {
                        viewPort.onSettleComplete()
                        offsetX.snapTo(0f)
                    }
                }
            }
    }

    LaunchedEffect(viewPort) {
        snapshotFlow { viewPort.targetProgress.value to viewPort.isDragging.value }
            .distinctUntilChanged()
            .collect { (target, dragging) ->
                if (dragging) {
                    progress.snapTo(target)
                } else {
                    progress.animateTo(
                        targetValue = target,
                        animationSpec = tween(
                            durationMillis = 250,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                }
            }
    }

    val centralWeeks = remember(stripDate, weekFields) {
        stripWeeks(stripDate, weekFields)
    }
    val centralLayout = stripLayoutRemember(
        weeks = centralWeeks,
        activeDate = stripDate,
        config = config,
    )

    val anchorColumnOffsetProvider: () -> Float = remember(centralLayout, density) {
        {
            with(density) {
                centralLayout.rowColumnOffsetY(progress.value).toPx()
            }
        }
    }
    val rowHeightPx = with(density) { centralLayout.rowHeight.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(centralLayout.monthHeight)
            .background(MaterialTheme.colorScheme.surface)
            .clipToBounds()
            .onSizeChanged {
                measuredWidthPx = it.width
                viewPort.onWidthChanged(it.width)
            }
            .stripGesture(
                port = gesturePort,
                thresholdPx = thresholdPx,
                fullScrollPx = fullScrollPx,
                stripDate = stripDate,
                onTap = { offset ->
                    if (abs(offsetX.value) <= 0.5f) {
                        val date = stripDateAt(
                            weeks = centralWeeks,
                            x = offset.x,
                            y = offset.y,
                            widthPx = measuredWidthPx,
                            columnOffsetPx = anchorColumnOffsetProvider(),
                            rowHeightPx = rowHeightPx,
                        )
                        if (date != null) {
                            viewPort.onDateSelected(date)
                        }
                    }
                },
            ),
    ) {
        val width = measuredWidthPx.toFloat()

        Box(modifier = Modifier.fillMaxSize()) {
            for (i in -config.pageRange..config.pageRange) {
                val pageDate = stripShiftDate(
                    date = stripDate,
                    steps = i,
                    isMonthMode = true,
                )
                val pageWeeks = remember(pageDate, weekFields) {
                    stripWeeks(pageDate, weekFields)
                }
                val pageLayout: StripLayout = remember(pageWeeks, stripDate, config) {
                    val idx = pageWeeks.indexOfFirst { it.contains(stripDate) }.coerceAtLeast(0)
                    StripLayout(
                        activeWeekIndex = idx,
                        totalRows = pageWeeks.size,
                        rowHeight = config.rowHeight,
                    )
                }

                StripPage(
                    pageDate = pageDate,
                    selectedDate = calendarDate,
                    anchorDate = stripDate,
                    markedDates = markedDates,
                    progressState = progressState,
                    offsetXState = offsetXState,
                    pageIndex = i,
                    pageWidth = width,
                    weeks = pageWeeks,
                    layout = pageLayout,
                    config = config,
                )
            }
        }
    }
}