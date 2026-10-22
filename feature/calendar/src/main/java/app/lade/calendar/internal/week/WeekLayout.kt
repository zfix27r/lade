package app.lade.calendar.internal.week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.dates.CalendarDatesWeekModel
import app.lade.calendar.internal.dates.CalendarDatesWeekRow
import app.lade.calendar.internal.dates.buildCalendarDatesStyle
import app.lade.calendar.internal.drag.CalendarDragBehavior
import app.lade.calendar.internal.drag.CalendarDragContainer
import app.lade.calendar.internal.drag.CalendarDragDirection
import app.lade.calendar.internal.main.CalendarModeActions
import app.lade.calendar.internal.week.data.GRID_HOUR_FROM
import app.lade.calendar.internal.week.data.GRID_HOUR_TO
import app.lade.calendardata.api.models.CalendarDataModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
internal fun WeekLayout(
    visibleDate: StateFlow<LocalDate>,
    selectedDate: StateFlow<LocalDate>,
    weeks: StateFlow<List<CalendarDatesWeekModel>>,
    days: StateFlow<List<CalendarDataModel>>,
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

    val hourCount = (GRID_HOUR_TO - 12).coerceAtLeast(1)
    val hourRange = GRID_HOUR_FROM until 12


    val rowHeight = config.week.rowHeight
    val timelineContent: Dp = rowHeight * hourCount
    val density = LocalDensity.current

    var hourColumnWidth by remember { mutableStateOf(0.dp) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val viewport: Dp = (maxHeight - rowHeight).coerceAtLeast(0.dp)
        val maxOffset: Dp = (timelineContent - viewport).coerceAtLeast(0.dp)
        val maxOffsetPx = with(density) { maxOffset.toPx() }

        val horizontalBehavior = CalendarDragBehavior.pages(
            initial = 0f,
        )
        val verticalBehavior = CalendarDragBehavior.scroll(
            start = 0f,
            end = maxOffsetPx,
            initial = 0f,
            hasBounds = maxOffsetPx > 0f,
        )

        CalendarDragContainer(
            modifier = modifier,
            horizontal = horizontalBehavior,
            vertical = verticalBehavior,
        ) { holder ->
            val state by holder.state.collectAsStateWithLifecycle()

            val currentWeek = weeksValue.firstOrNull()
            val measured = hourColumnWidth > 0.dp

            if (measured && currentWeek != null) {
                CalendarDatesWeekRow(
                    week = currentWeek,
                    selectedDate = selected,
                    onDateClick = actions.onDateSelected,
                    style = stripStyle,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .height(rowHeight)
                        .padding(start = hourColumnWidth)
                        .graphicsLayer {
                            if (state.direction == CalendarDragDirection.HORIZONTAL) {
                                translationX = state.offset
                            }
                        },
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .wrapContentWidth()
                    .padding(top = rowHeight),
            ) {
                HoursColumn(
                    hourRange = hourRange,
                    config = config.week,
                    modifier = Modifier
                        .wrapContentWidth()
                        .requiredHeight(timelineContent)
                        .graphicsLayer {
                            translationY = state.offset
                        }
                        .onSizeChanged { hourColumnWidth = with(density) { it.width.toDp() } },
                )
            }

            if (measured && currentWeek != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxSize()
                        .padding(start = hourColumnWidth, top = rowHeight)
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
                            .requiredHeight(timelineContent)
                            .graphicsLayer {
                                when (state.direction) {
                                    CalendarDragDirection.HORIZONTAL -> translationX = state.offset
                                    CalendarDragDirection.VERTICAL -> translationY = state.offset
                                    CalendarDragDirection.NONE -> Unit
                                }
                            },
                    )
                }
            }
        }
    }
}