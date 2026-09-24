package app.lade.calendar.ui.component.list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.calendar.R
import app.lade.calendar.ui.component.EntryDayMarkActions
import app.lade.entrykind.EntryKind

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AgendaRow(
    agenda: AgendaModel,
    onOpenAgenda: () -> Unit,
    onMarkDone: () -> Unit,
    onMarkSkip: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    enableMarkHaptics: Boolean = true,
) {
    val entry = agenda.entry
    val haptic = LocalHapticFeedback.current
    val timeRange = buildString {
        entry.startTime?.let { append(it) }
        entry.endTime?.let { append("–").append(it) }
    }

    val handleMarkDone: () -> Unit = {
        if (enableMarkHaptics) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        onMarkDone()
    }
    val handleMarkSkip: () -> Unit = {
        if (enableMarkHaptics) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        onMarkSkip()
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
        trailingContent = if (entry.kind == EntryKind.HABIT) {
            {
                EntryDayMarkActions(
                    done = agenda.logs.any { (it.actualAmount ?: 0) > 0 },
                    onDone = handleMarkDone,
                    onSkip = handleMarkSkip,
                )
            }
        } else null,
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
            Text(
                text = entry.title.ifBlank { stringResource(R.string.calendar_time_block_untitled) },
                style = MaterialTheme.typography.bodyLarge,
            )
        },
    )
}