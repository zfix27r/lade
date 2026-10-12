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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.main.CalendarStateModel
import app.lade.calendardata.api.agenda.CalendarDataAgendaModel
import java.time.LocalDate

@Composable
internal fun TimelineLayout(
    state: CalendarStateModel,
    onOpenAgenda: (LocalDate, CalendarDataAgendaModel) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onGoalValueChange: (Long, LocalDate, Long, Int) -> Unit,
    onEntryLongPress: (LocalDate, CalendarDataAgendaModel) -> Unit,
    onTimelineScroll: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val listState = rememberLazyListState()
    val today = remember { LocalDate.now() }

    LaunchedEffect(listState, state.days) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val day = state.days.getOrNull(index) ?: return@collect
                onTimelineScroll(day.date)
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
                items = state.days,
                key = { "day-${it.date.toEpochDay()}" },
            ) { day ->
                TimelineDayRow(
                    day = day,
                    isToday = day.date == today,
                    onOpenAgenda = onOpenAgenda,
                    onGoalToggle = onGoalToggle,
                    onGoalValueChange = onGoalValueChange,
                    onEntryLongPress = onEntryLongPress,
                    config = config,
                )
            }
        }
    }
}