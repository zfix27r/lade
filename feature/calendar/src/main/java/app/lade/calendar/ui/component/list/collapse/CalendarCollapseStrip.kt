package app.lade.calendar.ui.component.list.collapse

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarListStripMode
import app.lade.calendar.ui.component.swipe.SwipeResult
import app.lade.calendar.ui.component.swipe.awaitSwipe
import app.lade.calendardata.api.CalendarCardModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

@Composable
fun CalendarCollapseStrip(
    currentDate: LocalDate,
    entries: List<CalendarCardModel>,
    onDateSelected: (LocalDate) -> Unit,
    onSwipe: (CalendarDateMode) -> Unit,
    onStripModeChange: (CalendarListStripMode) -> Unit,
    stripMode: CalendarListStripMode,
    progress: Animatable<Float, *>,
    metrics: CalendarCollapseMetrics,
    config: CalendarCollapseConfig,
    fullScrollPx: Float,
    modifier: Modifier = Modifier,
) {
    val progressValue by progress.asState()
    val datesWithEntries = entries.map { it.date }.toSet()
    val minAlpha = config.inactiveRowMinAlpha
    val density = LocalDensity.current
    val thresholdPx = with(density) { 8.dp.toPx() }
    val scope = rememberCoroutineScope()

    val offsetX = remember { Animatable(0f) }
    var widthPx by remember { mutableIntStateOf(0) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(metrics.stripHeight())
            .background(MaterialTheme.colorScheme.surface)
            .clipToBounds()
            .onSizeChanged { widthPx = it.width }
            .pointerInput(stripMode, thresholdPx, widthPx) {
                awaitPointerEventScope {
                    while (true) {
                        val result = awaitSwipe(
                            thresholdPx = thresholdPx,
                            onDrag = { isVertical, delta ->
                                if (isVertical) {
                                    scope.launch {
                                        val next = (progress.value - delta / fullScrollPx)
                                            .coerceIn(0f, 1f)
                                        progress.snapTo(next)
                                    }
                                } else {
                                    scope.launch {
                                        offsetX.snapTo(offsetX.value + delta)
                                    }
                                }
                            },
                        )
                        when (result) {
                            is SwipeResult.Vertical -> {
                                val target = if (progress.value > 0.5f) 1f else 0f
                                scope.launch {
                                    progress.animateTo(target, tween(200))
                                    if (target == 1f && stripMode == CalendarListStripMode.WEEK) {
                                        onStripModeChange(CalendarListStripMode.MONTH)
                                    } else if (target == 0f && stripMode == CalendarListStripMode.MONTH) {
                                        onStripModeChange(CalendarListStripMode.WEEK)
                                    }
                                }
                            }
                            is SwipeResult.Horizontal -> {
                                val width = widthPx.toFloat()
                                val half = width / 2f
                                when {
                                    offsetX.value > half -> scope.launch {
                                        offsetX.animateTo(width, tween(200))
                                        onSwipe(CalendarDateMode.BACKWARD)
                                        offsetX.snapTo(0f)
                                    }
                                    offsetX.value < -half -> scope.launch {
                                        offsetX.animateTo(-width, tween(200))
                                        onSwipe(CalendarDateMode.FORWARD)
                                        offsetX.snapTo(0f)
                                    }
                                    else -> scope.launch {
                                        offsetX.animateTo(0f, tween(200))
                                    }
                                }
                            }
                            SwipeResult.Tap -> {
                                scope.launch { offsetX.animateTo(0f, tween(200)) }
                            }
                        }
                    }
                }
            },
    ) {
        val offsetValue by offsetX.asState()
        val width = constraints.maxWidth.toFloat()

        Box(modifier = Modifier.fillMaxSize()) {
            PageContent(
                pageDate = pageDate(currentDate, stripMode, -1),
                anchorDate = currentDate,
                datesWithEntries = datesWithEntries,
                onDateSelected = onDateSelected,
                progressValue = progressValue,
                stripMode = stripMode,
                config = config,
                minAlpha = minAlpha,
                fullScrollPx = fullScrollPx,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset((offsetValue - width).roundToInt(), 0) },
            )
            PageContent(
                pageDate = pageDate(currentDate, stripMode, 0),
                anchorDate = currentDate,
                datesWithEntries = datesWithEntries,
                onDateSelected = onDateSelected,
                progressValue = progressValue,
                stripMode = stripMode,
                config = config,
                minAlpha = minAlpha,
                fullScrollPx = fullScrollPx,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetValue.roundToInt(), 0) },
            )
            PageContent(
                pageDate = pageDate(currentDate, stripMode, 1),
                anchorDate = currentDate,
                datesWithEntries = datesWithEntries,
                onDateSelected = onDateSelected,
                progressValue = progressValue,
                stripMode = stripMode,
                config = config,
                minAlpha = minAlpha,
                fullScrollPx = fullScrollPx,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset((offsetValue + width).roundToInt(), 0) },
            )
        }
    }
}

@Composable
private fun PageContent(
    pageDate: LocalDate,
    anchorDate: LocalDate,
    datesWithEntries: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    progressValue: Float,
    stripMode: CalendarListStripMode,
    config: CalendarCollapseConfig,
    minAlpha: Float,
    fullScrollPx: Float,
    modifier: Modifier = Modifier,
) {
    val pageMetrics = rememberCalendarCollapseMetrics(
        pageDate = pageDate,
        anchorDate = anchorDate,
        stripMode = stripMode,
        config = config,
        fullScrollPx = fullScrollPx,
    )
    Column(
        modifier = modifier
            .offset { IntOffset(0, pageMetrics.columnOffsetY(progressValue).roundToPx()) },
    ) {
        pageMetrics.weeks.forEachIndexed { index, week ->
            val isActiveWeek = index == pageMetrics.activeWeekIndex
            val rowAlpha = if (isActiveWeek) 1f
            else minAlpha + (1f - minAlpha) * progressValue
            CalendarCollapseMonthRow(
                week = week,
                currentDate = anchorDate,
                datesWithEntries = datesWithEntries,
                onDateSelected = onDateSelected,
                rowAlpha = rowAlpha,
                horizontalPadding = config.rowHorizontalPadding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(pageMetrics.rowHeight),
            )
        }
    }
}

private fun pageDate(
    anchor: LocalDate,
    stripMode: CalendarListStripMode,
    offset: Int,
): LocalDate = when (stripMode) {
    CalendarListStripMode.WEEK -> anchor.plusWeeks(offset.toLong())
    CalendarListStripMode.MONTH -> {
        val ym = YearMonth.from(anchor).plusMonths(offset.toLong())
        ym.atDay(anchor.dayOfMonth.coerceAtMost(ym.lengthOfMonth()))
    }
}