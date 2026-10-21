package app.lade.calendar.internal.week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.dates.CalendarDatesWeekModel
import app.lade.calendar.internal.dates.CalendarDatesWeekRow
import app.lade.calendar.internal.dates.buildCalendarDatesStyle
import app.lade.calendar.internal.drag.CalendarDragContainer
import app.lade.calendar.internal.drag.CalendarDragDirection
import app.lade.calendar.internal.main.CalendarModeActions
import app.lade.calendar.internal.week.data.GRID_HOUR_FROM
import app.lade.calendar.internal.week.data.GRID_HOUR_TO
import app.lade.calendardata.api.agenda.CalendarDataDayModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
internal fun WeekLayout(
    visibleDate: StateFlow<LocalDate>,
    selectedDate: StateFlow<LocalDate>,
    weeks: StateFlow<List<CalendarDatesWeekModel>>,
    days: StateFlow<List<CalendarDataDayModel>>,
    actions: CalendarModeActions,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val visible by visibleDate.collectAsStateWithLifecycle()
    val selected by selectedDate.collectAsStateWithLifecycle()
    val daysValue by days.collectAsStateWithLifecycle()
    val weeksValue by weeks.collectAsStateWithLifecycle()
    val agendasByDate = daysValue.associate { it.date to it.agendas }
    val today = LocalDate.now()

    val stripStyle = buildCalendarDatesStyle(compact = true, visibleWeeks = 1)
    val hourCount = (GRID_HOUR_TO - GRID_HOUR_FROM).coerceAtLeast(1)
    val density = LocalDensity.current
    val hourHeightPx = with(density) { config.week.hourHeight.roundToPx() }
    val timelineContentPx = hourHeightPx * hourCount

    var hourColumnWidthPx by remember { mutableFloatStateOf(0f) }
    var timelineViewportPx by remember { mutableFloatStateOf(0f) }

    println(timelineViewportPx)
    println(timelineContentPx)


    CalendarDragContainer(modifier = modifier) { holder ->
        val state by holder.state.collectAsStateWithLifecycle()

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val pageWidthPx = with(density) { maxWidth.toPx() }
            val maxOffsetYPx = (timelineContentPx - timelineViewportPx).coerceAtLeast(0f)

            val offsetX = when (state.direction) {
                CalendarDragDirection.HORIZONTAL ->
                    state.totalOffset.coerceIn(-pageWidthPx, pageWidthPx)
                else -> 0f
            }
            val offsetY = when (state.direction) {
                CalendarDragDirection.VERTICAL ->
                    state.totalOffset.coerceIn((-timelineContentPx).toFloat(), 0f)
                else -> 0f
            }

            val hourColumnWidthDp = with(density) { hourColumnWidthPx.toDp() }
            val stripHeight = config.week.stripHeight
            val currentWeek = weeksValue.firstOrNull()
            val measured = hourColumnWidthPx > 0f

            if (measured && currentWeek != null) {
                CalendarDatesWeekRow(
                    week = currentWeek,
                    selectedDate = selected,
                    onDateClick = actions.onDateSelected,
                    style = stripStyle,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .height(stripHeight)
                        .padding(start = hourColumnWidthDp)
                        .graphicsLayer { translationX = offsetX },
                )
            }

            HoursColumn(
                config = config.week,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.TopStart)
                    .padding(top = stripHeight)
                    .offset { IntOffset(0, stripHeight.roundToPx()) }
                    .graphicsLayer { translationY = offsetY }
                    .onSizeChanged { hourColumnWidthPx = it.width.toFloat() },
            )

            // Слой 3: timeline.
            if (measured && currentWeek != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxSize()
                        .padding(start = hourColumnWidthDp, top = stripHeight)
                        .onSizeChanged { timelineViewportPx = it.height.toFloat() }
                        .clipToBounds(),
                ) {
                    WeekTimeline(
                        week = currentWeek,
                        agendasByDate = agendasByDate,
                        today = today,
                        selectedDate = visible,
                        onOpenDay = actions.onDateSelected,
                        config = config.week,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(config.week.hourHeight * hourCount)
                            .graphicsLayer {
                                translationX = offsetX
                                translationY = offsetY
                            },
                    )
                }
            }
        }
    }
}