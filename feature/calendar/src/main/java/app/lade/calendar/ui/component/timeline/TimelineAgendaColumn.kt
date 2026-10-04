package app.lade.calendar.ui.component.timeline

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
import androidx.compose.ui.unit.dp
import app.lade.calendar.domain.TimelineDay
import app.lade.calendar.ui.component.list.components.AgendaCard
import app.lade.calendar.ui.component.list.components.AgendaRow
import app.lade.calendardata.api.CalendarCardModel
import app.lade.entry.ui.color
import java.time.LocalDate

@Composable
fun TimelineAgendaColumn(
    day: TimelineDay,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onToggleDone: (Long, LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarTimelineConfig,
) {
    if (day.entries.isEmpty() && !config.showEmptyDays) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(config.daySpacing),
    ) {
        if (day.entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
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
                AgendaCard(
                    cornerRadius = config.cardCornerRadius,
                    stripeColor = card.entryKind.color(),
                    stripeWidth = config.cardStripeWidth,
                    containerColor = containerColor,
                ) {
                    AgendaRow(
                        card = card,
                        onOpenAgenda = { onOpenAgenda(card) },
                        onToggleDone = { onToggleDone(card.entryId, card.date) },
                        onGoalToggle = { goalId -> onGoalToggle(card.entryId, card.date, goalId) },
                        onLongPress = { onEntryLongPress(card) },
                        enableMarkHaptics = config.enableMarkHaptics,
                    )
                }
            }
        }
    }
}