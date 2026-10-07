package app.lade.calendar.internal.list.strip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.list.strip.anim.StripController
import app.lade.calendar.internal.list.strip.data.StripState
import app.lade.calendar.internal.list.strip.data.stripWeeks
import app.lade.calendar.internal.list.strip.layout.StripLayout
import app.lade.calendar.internal.list.strip.layout.stripDateAt
import app.lade.calendar.internal.list.strip.layout.stripLayoutRemember
import app.lade.calendar.internal.swipe.CalendarSwipeResult
import app.lade.calendar.internal.swipe.calendarSwipe
import app.lade.calendardata.api.DayProgress
import java.time.LocalDate
import java.time.temporal.WeekFields
import kotlin.math.abs

@Composable
internal fun StripSwipeRow(
    currentDate: LocalDate,
    stripState: StripState,
    offsetXState: State<Float>,
    progressState: State<Float>,
    controller: StripController,
    markedDates: Map<LocalDate, DayProgress>,
    onDateSelected: (LocalDate) -> Unit,
    config: StripConfig,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }
    val thresholdPx = with(density) { 8.dp.toPx() }
    var widthPx by remember { mutableIntStateOf(0) }

    val centralWeeks = remember(stripState.date, weekFields) {
        stripWeeks(stripState.date, weekFields)
    }
    val centralLayout = stripLayoutRemember(
        weeks = centralWeeks,
        activeDate = currentDate,
        config = config,
    )

    val anchorColumnOffsetProvider: () -> Float = remember(centralLayout, progressState, density) {
        { with(density) { centralLayout.rowColumnOffsetY(progressState.value).toPx() } }
    }
    val rowHeightPx = with(density) { centralLayout.rowHeight.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(centralLayout.monthHeight)
            .background(MaterialTheme.colorScheme.surface)
            .clipToBounds()
            .onSizeChanged {
                widthPx = it.width
                controller.onWidthChanged(it.width)
            }
            .calendarSwipe(
                thresholdPx = thresholdPx,
                consumeVertical = false,
                consumeHorizontal = true,
                onDrag = { isVertical, delta ->
                    if (!isVertical) controller.gesture.onDrag(delta)
                },
                onResult = { result ->
                    when (result) {
                        is CalendarSwipeResult.Tap -> {
                            if (abs(offsetXState.value) > 0.5f) return@calendarSwipe
                            val date = stripDateAt(
                                weeks = centralWeeks,
                                x = result.x,
                                y = result.y,
                                widthPx = widthPx,
                                columnOffsetPx = anchorColumnOffsetProvider(),
                                rowHeightPx = rowHeightPx,
                            )
                            if (date != null) onDateSelected(date)
                        }

                        is CalendarSwipeResult.Horizontal -> controller.gesture.onRelease()
                        is CalendarSwipeResult.Vertical -> controller.gesture.onRelease()
                    }
                },
            ),
    ) {
        val width = widthPx.toFloat()

        Box(modifier = Modifier.fillMaxSize()) {
            for (i in -config.pageRange..config.pageRange) {
                val pageDate = stripState.dateAt(i)
                val pageWeeks = remember(pageDate, weekFields) {
                    stripWeeks(pageDate, weekFields)
                }
                val pageLayout: StripLayout = remember(pageWeeks, currentDate, config) {
                    val idx = pageWeeks.indexOfFirst { it.contains(currentDate) }.coerceAtLeast(0)
                    StripLayout(
                        activeWeekIndex = idx,
                        totalRows = pageWeeks.size,
                        rowHeight = config.rowHeight,
                    )
                }

                StripPage(
                    pageDate = pageDate,
                    anchorDate = currentDate,
                    markedDates = markedDates,
                    progressState = progressState,
                    weeks = pageWeeks,
                    layout = pageLayout,
                    config = config,
                    offsetProvider = { offsetXState.value + i * width },
                )
            }
        }
    }
}