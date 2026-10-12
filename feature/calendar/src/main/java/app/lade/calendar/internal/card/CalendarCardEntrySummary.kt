package app.lade.calendar.internal.card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lade.calendar.R
import app.lade.calendardata.api.agenda.CalendarDataAgendaModel
import app.lade.entry.ui.icon
import app.lade.ui.theme.IconSizes
import app.lade.ui.theme.Row
import app.lade.ui.theme.Spacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CalendarCardEntrySummary(
    agenda: CalendarDataAgendaModel,
    onOpenAgenda: () -> Unit,
    onLongPress: () -> Unit,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val timeRange = agenda.entry.timeRangeString()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Row.lg)
            .combinedClickable(
                onClick = onOpenAgenda,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress()
                },
            )
            .padding(horizontal = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Icon(
            imageVector = agenda.entry.kind.icon(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(IconSizes.md),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = agenda.entry.title.ifBlank {
                    stringResource(R.string.calendar_time_block_untitled)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            if (timeRange.isNotEmpty()) {
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1,
                )
            }
        }

        CalendarCardEntryIndicator(
            agenda = agenda,
            onToggleExpand = onToggleExpand,
        )
    }
}