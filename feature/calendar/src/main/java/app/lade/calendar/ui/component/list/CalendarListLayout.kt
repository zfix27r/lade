package app.lade.calendar.ui.component.list

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarListStripMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseStrip
import app.lade.calendar.ui.component.list.collapse.rememberCalendarCollapseMetrics
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate

@Composable
fun CalendarListLayout(
    state: CalendarStateModel,
    onSwipe: (CalendarDateMode) -> Unit,
    onStripModeChange: (CalendarListStripMode) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onToggleDone: (entryId: Long, date: LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarListConfig = DefaultCalendarListConfig,
) {
    val collapseProgress = remember { Animatable(0f) }
    val density = LocalDensity.current

    val fullScrollPx = with(density) {
        (config.collapse.rowHeight * config.collapse.fullScrollRows).toPx()
    }

    val metrics = rememberCalendarCollapseMetrics(
        pageDate = state.currentDate,
        anchorDate = state.currentDate,
        stripMode = state.stripMode,
        config = config.collapse,
        fullScrollPx = fullScrollPx,
    )
    val progressValue by collapseProgress.asState()
    val listTopOffset = metrics.listTopOffset(progressValue)
    val panelHeight = config.listBottomPaddingForInputBar

    Box(modifier = modifier.fillMaxSize()) {
        CalendarListContent(
            entries = state.entries,
            topOffset = listTopOffset,
            fullScrollPx = fullScrollPx,
            progress = collapseProgress,
            config = config,
            panelHeight = panelHeight,
            onOpenAgenda = onOpenAgenda,
            onEntryLongPress = onEntryLongPress,
            onToggleDone = onToggleDone,
            onGoalToggle = onGoalToggle,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        ) {
            CalendarCollapseStrip(
                currentDate = state.currentDate,
                entries = state.entries,
                onDateSelected = onDateSelected,
                onSwipe = onSwipe,
                onStripModeChange = onStripModeChange,
                stripMode = state.stripMode,
                progress = collapseProgress,
                metrics = metrics,
                config = config.collapse,
                fullScrollPx = fullScrollPx,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}