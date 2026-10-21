package app.lade.calendar.internal.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.Dp
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.main.CalendarModeActions
import app.lade.calendar.internal.strip.CalendarStripView
import app.lade.calendar.internal.strip.rememberStripHostState
import app.lade.calendardata.api.agenda.CalendarDataDayModel
import app.lade.calendardata.api.mark.CalendarDataMarkModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun ListLayout(
    currentDate: StateFlow<LocalDate>,
    days: StateFlow<List<CalendarDataDayModel>>,
    marks: StateFlow<Map<LocalDate, CalendarDataMarkModel>>,
    actions: CalendarModeActions,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val listState = rememberLazyListState()
    val panelHeight = config.list.bottomPaddingForInputBar

    val density = LocalDensity.current
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }
    val fullScrollPx = with(density) {
        (config.strip.rowHeight * config.strip.scrollRows).toPx()
    }

    val host = rememberStripHostState(
        currentDate = currentDate,
        config = config.strip,
        listState = listState,
        weekFields = weekFields,
        fullScrollPx = fullScrollPx,
    )

    val topOffsetProvider: () -> Dp = remember(host) {
        {
            val layout = host.layout.value
            val progress = host.progress.value
            layout.listTopOffsetY(progress)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        CalendarStripView(
            host = host,
            currentDate = currentDate,
            days = days,
            marks = marks,
            actions = actions,
            onDateSelected = actions.onDateSelected,
            config = config.strip,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(host.connection),
        ) {
            ListContent(
                days = days,
                topOffsetProvider = topOffsetProvider,
                config = config.list,
                panelHeight = panelHeight,
                listState = listState,
                onOpenAgenda = actions.onOpenAgenda,
                onEntryLongPress = actions.onEntryLongPress,
                onGoalToggle = actions.onGoalToggle,
                onGoalValueChange = actions.onGoalValueChange,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}