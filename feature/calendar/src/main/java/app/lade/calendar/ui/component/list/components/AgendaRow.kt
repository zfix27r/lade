package app.lade.calendar.ui.component.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.entry.EntryKind
import app.lade.calendar.R
import app.lade.calendar.ui.component.EntryDayMarkActions

@Composable
fun AgendaRow(
    agenda: AgendaModel,
    onEditEntry: (entryId: Long) -> Unit,
    onMarkDone: () -> Unit,
    onMarkSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val entry = agenda.entry
    ListItem(
        modifier = modifier.fillMaxWidth(),
        leadingContent = null,
        trailingContent = if (entry.kind == EntryKind.HABIT) {
            {
                EntryDayMarkActions(
                    done = agenda.logs.any { (it.actualAmount ?: 0) > 0 },
                    onDone = onMarkDone,
                    onSkip = onMarkSkip,
                )
            }
        } else null,
        overlineContent = null,
        supportingContent = {
            Text(
                buildString {
                    entry.startTime?.let { append(it) }
                    entry.endTime?.let { append("–").append(it) }
                },
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { onEditEntry(entry.id) },
            )
        },
        colors = ListItemDefaults.colors(),
        elevation = ListItemDefaults.elevation(ListItemDefaults.Elevation),
        content = {
            Text(
                text = entry.title.ifBlank { stringResource(R.string.calendar_time_block_untitled) },
                modifier = Modifier.clickable { onEditEntry(entry.id) },
            )
        },
    )
}