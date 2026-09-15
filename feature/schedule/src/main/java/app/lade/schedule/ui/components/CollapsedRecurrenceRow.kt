package app.lade.schedule.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.resources.R
import app.lade.schedule.ui.label
import app.lade.temporal.api.RecurrenceDraft
import app.lade.temporal.api.RecurrencePreset

/** Collapsed: add icon + summary; full RRULE editor in a bottom sheet. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsedRecurrenceRow(
    draft: RecurrenceDraft,
    presets: List<RecurrencePreset>,
    onDraftChange: (RecurrenceDraft) -> Unit,
) {
    var sheetOpen by remember { mutableStateOf(false) }
    var draftInSheet by remember(draft) { mutableStateOf(draft) }
    OutlinedButton(
        onClick = {
            draftInSheet = draft
            sheetOpen = true
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.temporal_recurrence_add),
            )
            Text(draft.label())
        }
    }
    if (sheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { sheetOpen = false },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.screen_padding)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
            ) {
                Text(
                    text = stringResource(R.string.temporal_recurrence_sheet_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                RecurrenceEditor(
                    draft = draftInSheet,
                    presets = presets,
                    onDraftChange = { draftInSheet = it },
                )
                Button(
                    onClick = {
                        onDraftChange(draftInSheet)
                        sheetOpen = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = draftInSheet.hasValidDays(),
                ) {
                    Text(stringResource(R.string.temporal_recurrence_done))
                }
            }
        }
    }
}