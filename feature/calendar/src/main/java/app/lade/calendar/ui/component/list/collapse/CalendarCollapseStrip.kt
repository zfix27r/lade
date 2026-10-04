package app.lade.calendar.ui.component.list.collapse

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.ui.component.swipe.SwipeResult
import app.lade.calendar.ui.component.swipe.calendarSwipe
import app.lade.calendardata.api.DayProgress
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

@Composable
fun CalendarCollapseStrip(
    currentDate: LocalDate,
    markedDates: Map<LocalDate, DayProgress>,
    onDateSelected: (LocalDate) -> Unit,
    onSwipe: (CalendarDateMode, isMonth: Boolean) -> Unit,
    progress: Float,
    metrics: CalendarCollapseMetrics,
    config: CalendarCollapseConfig,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val thresholdPx = with(density) { 8.dp.toPx() }
    val rowHeightPx = with(density) { metrics.rowHeight.toPx() }
    val scope = rememberCoroutineScope()
    var offsetX by remember { mutableFloatStateOf(0f) }
    var widthPx by remember { mutableIntStateOf(0) }

    val columnOffsetPx = with(density) { metrics.columnOffsetY(progress).toPx() }
    val isMonthMode = progress >= 0.5f

    println("STRIP markedDates=${markedDates.size} keys=${markedDates.keys}")

    val settleAnimation = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(metrics.monthHeight)
            .background(MaterialTheme.colorScheme.surface)
            .clipToBounds()
            .onSizeChanged { widthPx = it.width }
            .calendarSwipe(
                thresholdPx = thresholdPx,
                consumeVertical = false,
                consumeHorizontal = true,
                onDrag = { isVertical, delta ->
                    if (!isVertical) offsetX += delta
                },
                onResult = { result ->
                    when (result) {
                        is SwipeResult.Tap -> {
                            offsetX = 0f
                            val cellWidth = widthPx / 7f
                            if (cellWidth > 0f) {
                                val col = (result.x / cellWidth).toInt().coerceIn(0, 6)
                                val row = ((result.y - columnOffsetPx) / rowHeightPx).toInt()
                                val date = metrics.weeks.getOrNull(row)?.getOrNull(col)
                                if (date != null) onDateSelected(date)
                            }
                        }
                        is SwipeResult.Horizontal -> {
                            val width = widthPx.toFloat()
                            val target = when {
                                result.totalX > thresholdPx -> width
                                result.totalX < -thresholdPx -> -width
                                offsetX > width / 2f -> width
                                offsetX < -width / 2f -> -width
                                else -> 0f
                            }
                            if (target != 0f) {
                                val direction = if (target > 0f) CalendarDateMode.BACKWARD
                                else CalendarDateMode.FORWARD
                                scope.launch {
                                    animate(
                                        initialValue = offsetX,
                                        targetValue = target,
                                        animationSpec = settleAnimation,
                                    ) { value, _ -> offsetX = value }
                                    onSwipe(direction, isMonthMode)
                                    offsetX = 0f
                                }
                            } else {
                                scope.launch {
                                    animate(
                                        initialValue = offsetX,
                                        targetValue = 0f,
                                        animationSpec = settleAnimation,
                                    ) { value, _ -> offsetX = value }
                                }
                            }
                        }
                        is SwipeResult.Vertical -> {
                            offsetX = 0f
                        }
                    }
                },
            ),
    ) {
        val width = widthPx.toFloat()
        val prevDate = pageDate(currentDate, isMonthMode, -1)
        val nextDate = pageDate(currentDate, isMonthMode, 1)

        Box(modifier = Modifier.fillMaxSize()) {
            StripPage(
                pageDate = prevDate,
                anchorDate = currentDate,
                markedDates = markedDates,
                progress = progress,
                config = config,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset((offsetX - width).roundToInt(), 0) },
            )
            StripPage(
                pageDate = currentDate,
                anchorDate = currentDate,
                markedDates = markedDates,
                progress = progress,
                config = config,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.roundToInt(), 0) },
            )
            StripPage(
                pageDate = nextDate,
                anchorDate = currentDate,
                markedDates = markedDates,
                progress = progress,
                config = config,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset((offsetX + width).roundToInt(), 0) },
            )
        }
    }
}

private fun pageDate(
    anchor: LocalDate,
    isMonthMode: Boolean,
    offset: Int,
): LocalDate = if (isMonthMode) {
    val ym = YearMonth.from(anchor).plusMonths(offset.toLong())
    ym.atDay(anchor.dayOfMonth.coerceAtMost(ym.lengthOfMonth()))
} else {
    anchor.plusWeeks(offset.toLong())
}

@Composable
private fun StripPage(
    pageDate: LocalDate,
    anchorDate: LocalDate,
    markedDates: Map<LocalDate, DayProgress>,
    progress: Float,
    config: CalendarCollapseConfig,
    modifier: Modifier,
) {
    val metrics = rememberCalendarCollapseMetrics(
        currentDate = pageDate,
        config = config,
    )
    val columnOffset = metrics.columnOffsetY(progress)

    val markedInPage = metrics.weeks.flatten().filter { markedDates.containsKey(it) }
    println("STRIP page=$pageDate, inPageMarked=${markedInPage.size} dates=$markedInPage")

    Column(
        modifier = modifier.offset { IntOffset(0, columnOffset.roundToPx()) },
    ) {
        metrics.weeks.forEach { week ->
            CalendarCollapseMonthRow(
                week = week,
                currentDate = anchorDate,
                pageDate = pageDate,
                markedDates = markedDates,
                horizontalPadding = config.rowHorizontalPadding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(metrics.rowHeight),
            )
        }
    }
}