package app.lade.calendar.internal.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.data.MarkedDatesStore
import app.lade.calendar.internal.list.ListLayout
import app.lade.calendar.internal.month.CalendarMonthGrid
import app.lade.calendar.internal.swipe.CalendarDateSwipe
import app.lade.calendar.internal.swipe.CalendarSwipeAxis
import app.lade.calendar.internal.timeline.TimelineLayout
import app.lade.calendar.internal.week.WeekLayout
import app.lade.calendar.internal.year.YearGrid
import java.time.YearMonth

@Composable
internal fun CalendarModeContent(
    state: CalendarStateModel,
    markedDatesStore: MarkedDatesStore,
    actions: CalendarModeActions,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    when (state.mode) {
        CalendarMode.LIST -> ListLayout(
            state = state,
            markedDatesStore = markedDatesStore,
            onDateSelected = actions.onDateSelected,
            onOpenAgenda = actions.onOpenAgenda,
            onEntryLongPress = actions.onEntryLongPress,
            onGoalToggle = actions.onGoalToggle,
            onGoalValueChange = actions.onGoalValueChange,
            config = config,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.TIMELINE -> TimelineLayout(
            state = state,
            onOpenAgenda = actions.onOpenAgenda,
            onGoalToggle = actions.onGoalToggle,
            onGoalValueChange = actions.onGoalValueChange,
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
                marks = state.marks,
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
                marks = state.marks,
                onOpenMonth = actions.onOpenMonth,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}