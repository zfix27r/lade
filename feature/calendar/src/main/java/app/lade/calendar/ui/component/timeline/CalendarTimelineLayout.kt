package app.lade.calendar.ui.component.timeline

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
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarTimelineLayout(
    state: CalendarStateModel,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onToggleDone: (Long, LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onTimelineScroll: (LocalDate) -> Unit,
    onVisibleMonthChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarTimelineConfig = DefaultCalendarTimelineConfig,
) {
    val listState = rememberLazyListState()
    val today = remember { LocalDate.now() }

    LaunchedEffect(listState, state.timelineDays) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val day = state.timelineDays.getOrNull(index) ?: return@collect
                onTimelineScroll(day.date)
                onVisibleMonthChange(YearMonth.from(day.date))
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
                bottom = config.bottomPaddingForInputBar + config.contentBottomPadding,
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                items = state.timelineDays,
                key = { "day-${it.date.toEpochDay()}" },
            ) { day ->
                TimelineDayRow(
                    day = day,
                    isToday = day.date == today,
                    onOpenAgenda = onOpenAgenda,
                    onToggleDone = onToggleDone,
                    onGoalToggle = onGoalToggle,
                    onEntryLongPress = onEntryLongPress,
                    config = config,
                )
            }
        }
    }
}