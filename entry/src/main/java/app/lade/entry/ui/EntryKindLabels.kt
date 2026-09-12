package app.lade.entry.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.entry.domain.models.EntryKind
import app.lade.resources.R

@Composable
fun entryKindLabel(kind: EntryKind): String = stringResource(
	when (kind) {
		EntryKind.TASK -> R.string.entry_kind_task
		EntryKind.EVENT -> R.string.entry_kind_event
		EntryKind.HABIT -> R.string.entry_kind_habit
		EntryKind.SCHEDULE -> R.string.entry_kind_schedule
	},
)
