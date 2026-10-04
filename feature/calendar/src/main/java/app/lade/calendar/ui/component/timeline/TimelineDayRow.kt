package app.lade.calendar.ui.component.timeline

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.lade.calendar.domain.TimelineDay
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate

@Composable
fun TimelineDayRow(
    day: TimelineDay,
    isToday: Boolean,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onToggleDone: (Long, LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarTimelineConfig,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = config.dayVerticalPadding),
        verticalAlignment = Alignment.Top,
    ) {
        TimelineDateRail(
            day = day,
            isToday = isToday,
            width = config.railWidth,
            horizontalPadding = config.railHorizontalPadding,
        )
        TimelineAgendaColumn(
            day = day,
            onOpenAgenda = onOpenAgenda,
            onToggleDone = onToggleDone,
            onGoalToggle = onGoalToggle,
            onEntryLongPress = onEntryLongPress,
            modifier = Modifier
                .weight(1f)
                .padding(end = config.contentHorizontalPadding),
            config = config,
        )
    }
}