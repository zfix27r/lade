package app.lade.entrydetailsscreen.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.agenda.api.goal.GoalUnit
import app.lade.agenda.api.goal.labelRes
import app.lade.entrydetailsscreen.domain.model.GoalDraft
import app.lade.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalRow(
    draft: GoalDraft,
    onChange: (GoalDraft) -> Unit,
    onRemove: () -> Unit,
) {
    var unitExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
    ) {
        OutlinedTextField(
            value = draft.title,
            onValueChange = { onChange(draft.copy(title = it)) },
            label = { Text(stringResource(R.string.goal_field_title)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = draft.amountText,
            onValueChange = { onChange(draft.copy(amountText = it)) },
            label = { Text(stringResource(R.string.goal_field_amount)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        ExposedDropdownMenuBox(
            expanded = unitExpanded,
            onExpandedChange = { unitExpanded = it },
            modifier = Modifier.weight(1f),
        ) {
            OutlinedTextField(
                value = stringResource(draft.unit.labelRes()),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.goal_field_unit)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
                },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = unitExpanded,
                onDismissRequest = { unitExpanded = false },
            ) {
                GoalUnit.entries
                    .filter { it != GoalUnit.UNKNOWN }
                    .forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(stringResource(unit.labelRes())) },
                            onClick = {
                                onChange(draft.copy(unit = unit))
                                unitExpanded = false
                            },
                        )
                    }
            }
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.goal_remove))
        }
    }
}