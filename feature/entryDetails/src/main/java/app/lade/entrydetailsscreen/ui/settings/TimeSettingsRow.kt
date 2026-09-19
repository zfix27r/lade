package app.lade.entrydetailsscreen.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.lade.entrydetailsscreen.R
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSettingsRow(
    label: String,
    time: LocalTime?,
    onTimeChange: (LocalTime?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val initial = time ?: LocalTime.of(9, 0)

    SettingsRow(
        icon = Icons.Default.Schedule,
        label = label,
        value = time?.let { "%02d:%02d".format(it.hour, it.minute) }
            ?: stringResource(R.string.entry_settings_not_set),
        onClick = { expanded = !expanded },
    )

    if (expanded) {
        val pickerState = rememberTimePickerState(
            initialHour = initial.hour,
            initialMinute = initial.minute,
            is24Hour = true,
        )
        TimePicker(
            state = pickerState,
            modifier = Modifier.fillMaxWidth(),
        )
        LaunchedEffect(pickerState.hour, pickerState.minute) {
            val picked = LocalTime.of(pickerState.hour, pickerState.minute)
            if (picked != time) onTimeChange(picked)
        }
    }
}