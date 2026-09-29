package app.lade.entrydetailsscreen.ui.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.lade.entry.EntryKind
import app.lade.entry.ui.label
import app.lade.resources.R

@Composable
fun KindBadge(
    entryKind: EntryKind,
    isOverridden: Boolean,
    onSelect: (EntryKind?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(stringResource(entryKind.label())) },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.entry_kind_auto)) },
                onClick = {
                    expanded = false
                    onSelect(null)
                },
            )
            EntryKind.entries
                .filter { it != EntryKind.NOTE }
                .forEach { k ->
                    DropdownMenuItem(
                        text = { Text(stringResource(k.label())) },
                        onClick = {
                            expanded = false
                            onSelect(k)
                        },
                    )
                }
        }
    }
}