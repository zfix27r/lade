package app.lade.calendar.ui.component.list

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseConnection
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseStrip
import app.lade.calendar.ui.component.list.collapse.rememberCalendarCollapseMetrics
import app.lade.calendardata.api.CalendarCardModel
import app.lade.ui.gesture.rememberSnapToEdge
import java.time.LocalDate

@Composable
fun CalendarListLayout(
    state: CalendarStateModel,
    onSwipe: (CalendarDateMode, isMonth: Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onToggleDone: (entryId: Long, date: LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarListConfig = DefaultCalendarListConfig,
) {
    val listState = rememberLazyListState()
    val collapseProgress = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val fullScrollPx = with(density) {
        (config.collapse.rowHeight * config.collapse.fullScrollRows).toPx()
    }

    val snapToEdge = rememberSnapToEdge(collapseProgress, config.collapse.snapToEdge)

    val connection = remember(collapseProgress, listState, snapToEdge, scope, fullScrollPx) {
        CalendarCollapseConnection(
            progress = collapseProgress,
            listState = listState,
            snapToEdge = snapToEdge,
            scope = scope,
            fullScrollPx = fullScrollPx,
        )
    }

    val metrics = rememberCalendarCollapseMetrics(
        currentDate = state.currentDate,
        config = config.collapse,
    )
    val progressValue by collapseProgress.asState()
    val listTopOffset = metrics.listTopOffset(progressValue)
    val panelHeight = config.listBottomPaddingForInputBar

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(connection),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
            ) {
                CalendarCollapseStrip(
                    currentDate = state.currentDate,
                    markedDates = state.markedDates,
                    onDateSelected = onDateSelected,
                    onSwipe = onSwipe,
                    progress = progressValue,
                    metrics = metrics,
                    config = config.collapse,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            CalendarListContent(
                entries = state.entries,
                topOffset = listTopOffset,
                config = config,
                panelHeight = panelHeight,
                listState = listState,
                onOpenAgenda = onOpenAgenda,
                onEntryLongPress = onEntryLongPress,
                onToggleDone = onToggleDone,
                onGoalToggle = onGoalToggle,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}