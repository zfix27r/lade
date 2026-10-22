package app.lade.calendar.internal.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.main.CalendarModeActions
import app.lade.calendardata.api.models.CalendarDataModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
internal fun TimelineLayout(
    currentDate: StateFlow<LocalDate>,
    days: StateFlow<List<CalendarDataModel>>,
    marks: StateFlow<Map<LocalDate, CalendarDataMinModel>>,
    actions: CalendarModeActions,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val listState = rememberLazyListState()
    val today = remember { LocalDate.now() }
    val daysValue by days.collectAsState()

    LaunchedEffect(listState, daysValue) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val day = daysValue.getOrNull(index) ?: return@collect
                actions.onTimelineScroll(day.date)
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                bottom = config.timelineLayout.bottomPaddingForInputBar +
                        config.timelineLayout.contentBottomPadding,
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                items = daysValue,
                key = { "day-${it.date.toEpochDay()}" },
            ) { day ->
                TimelineDayRow(
                    day = day,
                    isToday = day.date == today,
                    onOpenAgenda = actions.onOpenAgenda,
                    onGoalToggle = actions.onGoalToggle,
                    onGoalValueChange = actions.onGoalValueChange,
                    onEntryLongPress = actions.onEntryLongPress,
                    config = config,
                )
            }
        }
    }
}