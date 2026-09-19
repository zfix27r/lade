package app.lade.draft.internal.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lade.draft.DraftAlarm
import app.lade.draft.DraftReminder
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private val reminderPresets = listOf(5, 15, 30, 60, 1440)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraftEditorReminderCard(
    reminders: List<DraftReminder>,
    alarms: List<DraftAlarm>,
    onRemindersChange: (List<DraftReminder>) -> Unit,
    onAlarmsChange: (List<DraftAlarm>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Напоминания
            Text("Напоминания")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                reminderPresets.forEach { minutes ->
                    val selected = reminders.any { it.minutesBefore == minutes }
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val next = if (selected) {
                                reminders.filterNot { it.minutesBefore == minutes }
                            } else {
                                reminders + DraftReminder(minutes)
                            }
                            onRemindersChange(next)
                        },
                        label = { Text(formatReminder(minutes)) },
                    )
                }
            }

            // Будильники
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Будильники", modifier = Modifier.weight(1f))
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }
            alarms.forEach { alarm ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alarm.time.format(timeFormatter),
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = {
                        onAlarmsChange(alarms - alarm)
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Удалить")
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        val pickerState = rememberTimePickerState(
            initialHour = 9,
            initialMinute = 0,
        )
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(pickerState.hour, pickerState.minute)
                    onAlarmsChange(alarms + DraftAlarm(time))
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

private fun formatReminder(minutes: Int): String = when (minutes) {
    5 -> "5 мин"
    15 -> "15 мин"
    30 -> "30 мин"
    60 -> "1 час"
    1440 -> "1 день"
    else -> "$minutes мин"
}