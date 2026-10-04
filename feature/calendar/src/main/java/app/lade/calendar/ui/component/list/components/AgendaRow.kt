package app.lade.calendar.ui.component.list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lade.calendar.R
import app.lade.calendardata.api.CalendarCardModel
import app.lade.calendardata.api.CalendarGoalModel
import app.lade.entry.EntryKind

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AgendaRow(
    card: CalendarCardModel,
    onOpenAgenda: () -> Unit,
    onToggleDone: () -> Unit,
    onGoalToggle: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    enableMarkHaptics: Boolean = true,
) {
    val haptic = LocalHapticFeedback.current
    val showMark = card.entryKind != EntryKind.NOTE && card.entryKind != EntryKind.SCHEDULE
    var expanded by remember(card.entryId) { mutableStateOf(false) }

    val timeRange = buildString {
        card.timeFrom?.let { append(it) }
        card.timeTo?.let { append("–").append(it) }
    }

    val pending = card.pendingGoals
    val visibleGoals = if (expanded) pending else pending.take(MAX_VISIBLE_GOALS)
    val hiddenCount = pending.size - visibleGoals.size

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
                        MarkCircle(done = card.allGoalsDone, onClick = handleToggle)
                    }
                }
                if (pending.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        visibleGoals.forEach { goal ->
                            GoalRow(
                                goal = goal,
                                onClick = { onGoalToggle(goal.id) },
                            )
                        }
                        if (hiddenCount > 0 && !expanded) {
                            ExpandRow(
                                text = "Показать все (${pending.size})",
                                onClick = { expanded = true },
                            )
                        }
                        if (expanded && pending.size > MAX_VISIBLE_GOALS) {
                            ExpandRow(
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

private const val MAX_VISIBLE_GOALS = 3