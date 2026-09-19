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
import app.lade.entrykind.EntryKind
import app.lade.entrykind.labelRes
import app.lade.resources.R

@Composable
fun KindBadge(
    kind: EntryKind,
    isOverridden: Boolean,
    onSelect: (EntryKind?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(stringResource(kind.labelRes())) },
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
                .filter { it != EntryKind.UNKNOWN }
                .forEach { k ->
                    DropdownMenuItem(
                        text = { Text(stringResource(k.labelRes())) },
                        onClick = {
                            expanded = false
                            onSelect(k)
                        },
                    )
                }
        }
    }
}