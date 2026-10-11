package app.lade.calendar.internal.list.card.expandable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.lade.calendar.R
import app.lade.calendar.internal.list.card.CardDaysLeftBadge
import app.lade.calendardata.api.CalendarCardModel
import app.lade.calendardata.api.timeRangeString

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CardExpandableSummary(
    card: CalendarCardModel,
    onOpenAgenda: () -> Unit,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
) {
    val haptic = LocalHapticFeedback.current
    val timeRange = card.timeRangeString()
    val daysLeft = card.daysLeft

    val handleLongPress: (() -> Unit)? = onLongPress?.let {
        {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            it()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .combinedClickable(
                    onClick = onOpenAgenda,
                    onLongClick = handleLongPress,
                ),
        ) {
            Text(
                text = card.title.ifBlank {
                    stringResource(R.string.calendar_time_block_untitled)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (timeRange.isNotEmpty()) {
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        if (!card.hasGoals && daysLeft != null) {
            CardDaysLeftBadge(daysLeft = daysLeft)
        }

        if (card.hasGoals) {
            CardExpandableGoalDots(
                total = card.goalsTotal,
                done = card.goalsDone,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onToggleExpand)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            )
        }
    }
}