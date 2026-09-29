package app.lade.calendar.ui.component.list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.calendar.R
import app.lade.entry.EntryKind

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AgendaRow(
    agenda: AgendaModel,
    onOpenAgenda: () -> Unit,
    onToggleDone: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    enableMarkHaptics: Boolean = true,
) {
    val entry = agenda.entry
    val haptic = LocalHapticFeedback.current
    val isDone = agenda.logs.any { (it.actualAmount ?: 0) > 0 }
    val showMark = entry.entryKind != EntryKind.NOTE && entry.entryKind != EntryKind.SCHEDULE

    val timeRange = buildString {
        entry.startTime?.let { append(it) }
        entry.endTime?.let { append("–").append(it) }
    }

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
        elevation = ListItemDefaults.elevation(ListItemDefaults.Elevation),
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = entry.title.ifBlank {
                        stringResource(R.string.calendar_time_block_untitled)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                if (showMark) {
                    MarkCircle(
                        done = isDone,
                        onClick = handleToggle,
                    )
                }
            }
        },
    )
}

@Composable
private fun MarkCircle(
    done: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (done) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Checkbox,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (done) color else Color.Transparent)
                .then(
                    if (done) Modifier else Modifier.background(
                        color = color.copy(alpha = 0.4f),
                        shape = CircleShape,
                    )
                ),
        )
        if (done) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}