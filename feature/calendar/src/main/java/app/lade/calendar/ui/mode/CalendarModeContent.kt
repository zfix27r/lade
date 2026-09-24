package app.lade.calendar.ui.mode

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.ui.component.list.CalendarListLayout
import app.lade.calendar.ui.component.month.CalendarMonthGrid
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
            onDateSelected = actions.onDateSelected,
            onOpenAgenda = actions.onOpenAgenda,
            onEntryLongPress = actions.onEntryLongPress,
            onMarkDone = actions.onMarkDone,
            onMarkSkip = actions.onMarkSkip,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.DAY,
        CalendarMode.DAY_3,
        CalendarMode.WEEK,
            -> CalendarWeekLayout(
            state = state,
            onSwipe = actions.onSwipe,
            onDateSelected = actions.onDateSelected,
            onEditEntry = actions.onEditEntry,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.MONTH -> CalendarMonthGrid(
            month = YearMonth.from(state.currentDate),
            entries = state.entries,
            selectedDate = state.currentDate,
            onSwipe = actions.onSwipe,
            onOpenDay = actions.onOpenDay,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.YEAR -> CalendarYearGrid(
            year = state.currentDate.year,
            entries = state.entries,
            onSwipe = actions.onSwipe,
            onOpenMonth = actions.onOpenMonth,
            modifier = modifier.fillMaxSize(),
        )
    }
}