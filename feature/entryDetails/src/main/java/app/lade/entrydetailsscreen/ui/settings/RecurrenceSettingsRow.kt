package app.lade.entrydetailsscreen.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.lade.entrydetailsscreen.R
import app.lade.schedule.ui.components.CollapsedRecurrenceRow
import app.lade.temporal.api.RecurrenceDraft
import app.lade.temporal.api.RecurrencePreset
import app.lade.schedule.ui.label

@Composable
fun RecurrenceSettingsRow(
    draft: RecurrenceDraft,
    presets: List<RecurrencePreset>,
    onDraftChange: (RecurrenceDraft) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    SettingsRow(
        icon = Icons.Default.Repeat,
        label = stringResource(R.string.entry_settings_recurrence),
        value = if (draft.preset == RecurrencePreset.None) {
            stringResource(R.string.entry_settings_not_set)
        } else {
            draft.preset.label()
        },
        onClick = { expanded = !expanded },
    )

    if (expanded) {
        CollapsedRecurrenceRow(
            draft = draft,
            presets = presets,
            onDraftChange = onDraftChange,
        )
    }
}