package app.lade.calendar.internal.ui.mode

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.domain.CalendarStateModel
import app.lade.calendar.internal.domain.mode.CalendarMode
import app.lade.calendar.internal.domain.mode.CalendarModeActions
import app.lade.calendar.internal.list.ListLayout
import app.lade.calendar.internal.list.strip.data.StripState
import app.lade.calendar.internal.list.strip.data.StripStateHolder
import app.lade.calendar.internal.swipe.CalendarDateSwipe
import app.lade.calendar.internal.swipe.CalendarSwipeAxis
import app.lade.calendar.internal.timeline.TimelineLayout
import app.lade.calendar.internal.ui.month.CalendarMonthGrid
import app.lade.calendar.internal.ui.year.YearGrid
import app.lade.calendar.internal.week.WeekLayout
import java.time.YearMonth

@Composable
internal fun CalendarModeContent(
    state: CalendarStateModel,
    stripState: StripState,
    strip: StripStateHolder,
    offsetXState: State<Float>,
    actions: CalendarModeActions,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    when (state.mode) {
        CalendarMode.LIST -> ListLayout(
            state = state,
            stripState = stripState,
            strip = strip,
            offsetXState = offsetXState,
            onDateSelected = actions.onDateSelected,
            onOpenAgenda = actions.onOpenAgenda,
            onEntryLongPress = actions.onEntryLongPress,
            onToggleDone = actions.onToggleDone,
            onGoalToggle = actions.onGoalToggle,
            config = config,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.TIMELINE -> TimelineLayout(
            state = state,
            onOpenAgenda = actions.onOpenAgenda,
            onToggleDone = actions.onToggleDone,
            onGoalToggle = actions.onGoalToggle,
            onEntryLongPress = actions.onEntryLongPress,
            onTimelineScroll = actions.onTimelineScroll,
            config = config,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.WEEK -> CalendarDateSwipe(
            onSwipe = { direction -> actions.onSwipe(direction, false) },
            axis = CalendarSwipeAxis.HORIZONTAL,
            modifier = modifier.fillMaxSize(),
        ) {
            WeekLayout(
                state = state,
                onDateSelected = actions.onDateSelected,
                onEditEntry = actions.onEditEntry,
                config = config,
                modifier = Modifier.fillMaxSize(),
            )
        }

        CalendarMode.MONTH -> CalendarDateSwipe(
            onSwipe = { direction -> actions.onSwipe(direction, true) },
            axis = CalendarSwipeAxis.VERTICAL,
            modifier = modifier.fillMaxSize(),
        ) {
            CalendarMonthGrid(
                month = YearMonth.from(state.currentDate),
                entries = state.entries,
                selectedDate = state.currentDate,
                onOpenDay = actions.onOpenDay,
                modifier = Modifier.fillMaxSize(),
            )
        }

        CalendarMode.YEAR -> CalendarDateSwipe(
            onSwipe = { direction -> actions.onSwipe(direction, true) },
            axis = CalendarSwipeAxis.VERTICAL,
            modifier = modifier.fillMaxSize(),
        ) {
            YearGrid(
                year = state.currentDate.year,
                entries = state.entries,
                onOpenMonth = actions.onOpenMonth,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}