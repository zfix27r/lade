package app.lade.calendar.internal.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.lade.calendar.internal.card.expandable.CardExpandableGoalDots
import app.lade.calendar.internal.card.timer.CardTimerIndicator

@Composable
internal fun CalendarCardEntryIndicator(
    agenda: CalendarDataFullAgendaModel,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val goals = agenda.goals
    val timer = agenda.timer
    val daysLeft = agenda.daysLeft
    val timeFrom = agenda.entry.timeFrom

    when {
        goals != null -> {
            CardExpandableGoalDots(
                total = goals.goalsTotal,
                done = goals.goalsDone,
                modifier = modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onToggleExpand)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            )
        }

        timer != null -> {
            CardTimerIndicator(
                timer = timer,
                modifier = modifier,
            )
        }

        daysLeft != null -> {
            CardDaysLeftBadge(
                daysLeft = daysLeft,
                modifier = modifier,
            )
        }

        timeFrom != null -> {
            Text(
                text = agenda.entry.timeRangeString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = modifier,
            )
        }
    }
}