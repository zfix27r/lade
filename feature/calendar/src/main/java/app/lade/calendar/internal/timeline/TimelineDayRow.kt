package app.lade.calendar.internal.timeline

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.timeline.data.TimelineDay
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate

@Composable
internal fun TimelineDayRow(
    day: TimelineDay,
    isToday: Boolean,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onToggleDone: (Long, LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = config.timelineLayout.dayPadding),
        verticalAlignment = Alignment.Top,
    ) {
        TimelineDateRail(
            day = day,
            isToday = isToday,
            config = config.timelineLayout,
        )
        TimelineAgendaColumn(
            day = day,
            onOpenAgenda = onOpenAgenda,
            onToggleDone = onToggleDone,
            onGoalToggle = onGoalToggle,
            onEntryLongPress = onEntryLongPress,
            modifier = Modifier
                .weight(1f)
                .padding(end = config.timelineLayout.contentPadding),
            config = config,
        )
    }
}