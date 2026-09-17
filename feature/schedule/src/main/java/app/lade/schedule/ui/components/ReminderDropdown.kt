package app.lade.schedule.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDropdown(
    minutes: Int?,
    onChange: (Int?) -> Unit,
) {
    val options = listOf(
        null to R.string.reminder_none,
        5 to R.string.reminder_5min,
        15 to R.string.reminder_15min,
        30 to R.string.reminder_30min,
        60 to R.string.reminder_1hour,
    )
    var expanded by remember { mutableStateOf(false) }
    val labelRes = options.find { it.first == minutes }?.second ?: R.string.reminder_none
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = stringResource(labelRes),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.temporal_field_reminder)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (value, titleRes) ->
                DropdownMenuItem(
                    text = { Text(stringResource(titleRes)) },
                    onClick = {
                        onChange(value)
                        expanded = false
                    },
                )
            }
        }
    }
}