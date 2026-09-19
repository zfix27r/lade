package app.lade.draft.internal.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lade.draft.DraftModel
import app.lade.draft.internal.bar.conf.DraftRecurrencePreset
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraftEditorTemporalCard(
    draft: DraftModel,
    onDateChange: (java.time.LocalDate?, java.time.LocalDate?) -> Unit,
    onTimeChange: (LocalTime?, LocalTime?) -> Unit,
    onRecurrenceChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateText = draft.dateFrom?.let { from ->
        draft.dateTo?.let { to -> "${from.format(dateFormatter)} – ${to.format(dateFormatter)}" }
            ?: from.format(dateFormatter)
    } ?: "Не задано"

    val timeText = draft.timeFrom?.let { from ->
        draft.timeEnd?.let { to -> "${from.format(timeFormatter)} – ${to.format(timeFormatter)}" }
            ?: from.format(timeFormatter)
    } ?: "Не задано"

    val recurrenceText = DraftRecurrencePreset.fromRrule(draft.rrule).displayName()

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Text(
                    text = dateText,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Изменить")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null)
                Text(
                    text = timeText,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = { showTimePicker = true }) {
                    Text("Изменить")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.Repeat, contentDescription = null)
                Text(
                    text = recurrenceText,
                    modifier = Modifier.weight(1f),
                )
            }
            DraftRecurrencePreset.entries.forEach { preset ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = DraftRecurrencePreset.fromRrule(draft.rrule) == preset,
                        onClick = { onRecurrenceChange(preset.rrule) },
                    )
                    Text(preset.displayName())
                }
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = draft.dateFrom
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = pickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    onDateChange(date, null)
                    showDatePicker = false
                }) { Text("ОК") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showTimePicker) {
        val pickerState = rememberTimePickerState(
            initialHour = draft.timeFrom?.hour ?: 0,
            initialMinute = draft.timeFrom?.minute ?: 0,
        )
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(pickerState.hour, pickerState.minute)
                    onTimeChange(time, null)
                    showTimePicker = false
                }) { Text("ОК") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
            },
        ) {
            TimePicker(state = pickerState)
        }
    }
}