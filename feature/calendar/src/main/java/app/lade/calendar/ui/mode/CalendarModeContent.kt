package app.lade.calendar.ui.mode

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.ui.component.list.CalendarListLayout
import app.lade.calendar.ui.component.month.CalendarMonthGrid
import app.lade.calendar.ui.component.swipe.CalendarDateSwipe
import app.lade.calendar.ui.component.swipe.SwipeAxis
import app.lade.calendar.ui.component.timeline.CalendarTimelineLayout
import app.lade.calendar.ui.component.week.CalendarWeekLayout
import app.lade.calendar.ui.component.year.CalendarYearGrid
import java.time.YearMonth

@Composable
fun CalendarModeContent(
    state: CalendarStateModel,
    actions: CalendarModeActions,
    modifier: Modifier = Modifier,
) {
    when (state.mode) {
        CalendarMode.LIST -> CalendarListLayout(
            state = state,
            onSwipe = actions.onSwipe,
            onStripModeChange = actions.onStripModeChange,
            onDateSelected = actions.onDateSelected,
            onOpenAgenda = actions.onOpenAgenda,
            onEntryLongPress = actions.onEntryLongPress,
            onToggleDone = actions.onToggleDone,
            onGoalToggle = actions.onGoalToggle,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.TIMELINE -> CalendarTimelineLayout(
            state = state,
            onOpenAgenda = actions.onOpenAgenda,
            onToggleDone = actions.onToggleDone,
            onGoalToggle = actions.onGoalToggle,
            onEntryLongPress = actions.onEntryLongPress,
            onTimelineScroll = actions.onTimelineScroll,
            onVisibleMonthChange = actions.onVisibleMonthChange,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.WEEK -> CalendarDateSwipe(
            onSwipe = actions.onSwipe,
            axis = SwipeAxis.HORIZONTAL,
            modifier = modifier.fillMaxSize(),
        ) {
            CalendarWeekLayout(
                state = state,
                onDateSelected = actions.onDateSelected,
                onEditEntry = actions.onEditEntry,
                modifier = Modifier.fillMaxSize(),
            )
        }

        CalendarMode.MONTH -> CalendarDateSwipe(
            onSwipe = actions.onSwipe,
            axis = SwipeAxis.VERTICAL,
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
            onSwipe = actions.onSwipe,
            axis = SwipeAxis.VERTICAL,
            modifier = modifier.fillMaxSize(),
        ) {
            CalendarYearGrid(
                year = state.currentDate.year,
                entries = state.entries,
                onOpenMonth = actions.onOpenMonth,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}