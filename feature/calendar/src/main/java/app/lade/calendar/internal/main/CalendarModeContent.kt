package app.lade.calendar.internal.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.dates.CalendarDatesHost
import app.lade.calendar.internal.dates.CalendarDatesWeekModel
import app.lade.calendar.internal.list.ListLayout
import app.lade.calendar.internal.timeline.TimelineLayout
import app.lade.calendar.internal.week.WeekLayout
import app.lade.calendar.internal.year.YearGrid
import app.lade.calendardata.api.models.CalendarDataModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
internal fun CalendarModeContent(
    mode: StateFlow<CalendarMode>,
    visibleDate: StateFlow<LocalDate>,
    selectedDate: StateFlow<LocalDate>,
    weeks: StateFlow<List<CalendarDatesWeekModel>>,
    days: StateFlow<List<CalendarDataModel>>,
    marks: StateFlow<Map<LocalDate, CalendarDataMinModel>>,
    actions: CalendarModeActions,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val modeState by mode.collectAsStateWithLifecycle()

    when (modeState) {
        CalendarMode.LIST -> ListLayout(
            currentDate = selectedDate,
            days = days,
            marks = marks,
            actions = actions,
            config = config,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.TIMELINE -> TimelineLayout(
            currentDate = selectedDate,
            days = days,
            marks = marks,
            actions = actions,
            config = config,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.WEEK -> WeekLayout(
            visibleDate = visibleDate,
            selectedDate = selectedDate,
            weeks = weeks,
            days = days,
            actions = actions,
            config = config,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.MONTH -> CalendarDatesHost(
            weeks = weeks,
            visibleDate = visibleDate,
            selectedDate = selectedDate,
            onDateClick = actions.onDateSelected,
            onVisibleDateChange = actions.onVisibleDateChange,
            modifier = modifier.fillMaxSize(),
        )

        CalendarMode.YEAR -> YearGrid(
            currentDate = selectedDate,
            marks = marks,
            actions = actions,
            config = config,
            modifier = Modifier.fillMaxSize(),
        )
    }
}