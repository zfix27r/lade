package app.lade.schedule.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.resources.R
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleTimeButton(
    label: String,
    time: LocalTime,
    onPicked: (LocalTime) -> Unit,
    onClear: () -> Unit,
    clearable: Boolean,
) {
    var open by remember { mutableStateOf(false) }
    val gap = dimensionResource(R.dimen.spacing_sm)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        OutlinedButton(
            onClick = { open = true },
            modifier = Modifier.weight(1f),
        ) { Text(label) }
        if (clearable) {
            OutlinedButton(onClick = onClear) {
                Text(stringResource(R.string.action_clear))
            }
        }
    }
    if (open) {
        val state = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPicked(LocalTime.of(state.hour, state.minute))
                        open = false
                    },
                ) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
            text = { TimePicker(state = state) },
        )
    }
}
