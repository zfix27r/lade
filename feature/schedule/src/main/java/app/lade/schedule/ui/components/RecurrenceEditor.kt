package app.lade.schedule.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.lade.resources.R
import app.lade.schedule.ui.DaysOfWeekSelector
import app.lade.schedule.ui.label
import app.lade.recurrence.api.RecurrenceDraft
import app.lade.recurrence.api.RecurrencePreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceEditor(
    draft: RecurrenceDraft,
    presets: List<RecurrencePreset>,
    onDraftChange: (RecurrenceDraft) -> Unit,
) {
    var presetExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = presetExpanded,
        onExpandedChange = { presetExpanded = it },
    ) {
        OutlinedTextField(
            value = draft.preset.label(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.temporal_field_type)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = presetExpanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = presetExpanded,
            onDismissRequest = { presetExpanded = false },
        ) {
            presets.forEach { preset ->
                DropdownMenuItem(
                    text = { Text(preset.label()) },
                    onClick = {
                        onDraftChange(draft.copy(preset = preset))
                        presetExpanded = false
                    },
                )
            }
        }
    }
    if (draft.preset == RecurrencePreset.EveryNDays ||
        draft.preset == RecurrencePreset.Weekly ||
        draft.preset == RecurrencePreset.Monthly ||
        draft.preset == RecurrencePreset.Yearly
    ) {
        OutlinedTextField(
            value = draft.interval.toString(),
            onValueChange = { text ->
                val n = text.filter { it.isDigit() }.toIntOrNull()?.coerceAtLeast(1) ?: 1
                onDraftChange(draft.copy(interval = n))
            },
            label = { Text(stringResource(R.string.temporal_field_interval)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
    }
    if (draft.preset == RecurrencePreset.Weekly) {
        Text(
            stringResource(R.string.temporal_field_days),
            style = MaterialTheme.typography.bodyMedium,
        )
        DaysOfWeekSelector(
            flags = draft.daysOfWeek,
            onFlagsChange = { onDraftChange(draft.copy(daysOfWeek = it)) },
        )
    }
}