package app.lade.calendar.internal.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lade.calendar.R
import app.lade.calendar.internal.list.data.ListGoalsVisibility
import app.lade.calendardata.api.CalendarCardModel
import app.lade.calendardata.api.timeRangeString
import app.lade.entry.EntryKind

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ListRow(
    card: CalendarCardModel,
    onOpenAgenda: () -> Unit,
    onToggleDone: () -> Unit,
    onGoalToggle: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    enableMarkHaptics: Boolean = true,
    goalsVisibilityLimit: Int = 3,
) {
    val haptic = LocalHapticFeedback.current
    val showMark = card.entryKind != EntryKind.NOTE && card.entryKind != EntryKind.SCHEDULE
    var expanded by remember(card.entryId) { mutableStateOf(false) }

    val timeRange = card.timeRangeString()

    val pending = card.pendingGoals
    val goalsVisibility = ListGoalsVisibility.of(pending, expanded, goalsVisibilityLimit)
    val visibleGoals = goalsVisibility.visible
    val hiddenCount = goalsVisibility.hiddenCount

    val handleToggle: () -> Unit = {
        if (enableMarkHaptics) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        onToggleDone()
    }
    val handleLongPress: (() -> Unit)? = onLongPress?.let {
        {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            it()
        }
    }

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onOpenAgenda,
                onLongClick = handleLongPress,
            ),
        leadingContent = null,
        trailingContent = null,
        overlineContent = null,
        supportingContent = if (timeRange.isNotEmpty()) {
            {
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else null,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = card.title.ifBlank {
                            stringResource(R.string.calendar_time_block_untitled)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    if (showMark && card.hasGoals) {
                        ListMarkCircle(done = card.allGoalsDone, onClick = handleToggle)
                    }
                }
                if (pending.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        visibleGoals.forEach { goal ->
                            ListGoalRow(
                                goal = goal,
                                onClick = { onGoalToggle(goal.id) },
                            )
                        }
                        if (hiddenCount > 0 && !expanded) {
                            StripExpandRow(
                                text = "Показать все (${pending.size})",
                                onClick = { expanded = true },
                            )
                        }
                        if (expanded && pending.size > goalsVisibilityLimit) {
                            StripExpandRow(
                                text = "Свернуть",
                                onClick = { expanded = false },
                            )
                        }
                    }
                }
            }
        },
    )
}