package app.lade.calendar.internal.timeline

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.list.ListCard
import app.lade.calendar.internal.list.ListRow
import app.lade.calendar.internal.timeline.data.TimelineDay
import app.lade.calendardata.api.CalendarCardModel
import app.lade.entry.ui.color
import java.time.LocalDate

@Composable
internal fun TimelineAgendaColumn(
    day: TimelineDay,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onToggleDone: (Long, LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val layout = config.timelineLayout
    val listConfig = config.list

    if (day.entries.isEmpty() && !layout.showEmptyDays) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(layout.daySpacing),
    ) {
        if (day.entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(layout.emptyDayHeight),
            )
        } else {
            day.entries.forEach { card ->
                val containerColor by animateColorAsState(
                    targetValue = if (card.allGoalsDone) {
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    },
                    label = "timelineCardContainerColor",
                )
                ListCard(
                    cornerRadius = layout.cardCornerRadius,
                    stripeColor = card.entryKind.color(),
                    stripeWidth = layout.cardStripeWidth,
                    containerColor = containerColor,
                ) {
                    ListRow(
                        card = card,
                        onOpenAgenda = { onOpenAgenda(card) },
                        onToggleDone = { onToggleDone(card.entryId, card.date) },
                        onGoalToggle = { goalId -> onGoalToggle(card.entryId, card.date, goalId) },
                        onLongPress = { onEntryLongPress(card) },
                        enableMarkHaptics = layout.enableMarkHaptics,
                        goalsVisibilityLimit = listConfig.goalsVisibilityLimit,
                    )
                }
            }
        }
    }
}