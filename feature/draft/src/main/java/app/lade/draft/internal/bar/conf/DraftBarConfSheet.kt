package app.lade.draft.internal.bar.conf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.DraftAlarm
import app.lade.draft.DraftReminder
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private val reminderPresets = listOf(5, 15, 30, 60, 1440)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraftBarConfSheet(
    onDismiss: () -> Unit,
    viewModel: DraftBarConfSheetViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showAlarmPicker by remember { mutableStateOf(false) }
    var dateTargetTo by remember { mutableStateOf(false) }
    var timeTargetTo by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Дата ──
            Section(title = "Дата") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.dateFrom?.toString() ?: "Не задано",
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = {
                        dateTargetTo = false
                        showDatePicker = true
                    }) { Text("Выбрать") }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Диапазон", modifier = Modifier.weight(1f))
                    Switch(
                        checked = state.isRangeDate,
                        onCheckedChange = viewModel::onRangeDateToggle,
                    )
                }
                if (state.isRangeDate) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.dateTo?.toString() ?: "Конец не задан",
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = {
                            dateTargetTo = true
                            showDatePicker = true
                        }) { Text("Выбрать") }
                    }
                }
            }

            // ── Время ──
            Section(title = "Время") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.timeFrom?.format(timeFormatter) ?: "Не задано",
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = {
                        timeTargetTo = false
                        showTimePicker = true
                    }) { Text("Выбрать") }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Диапазон", modifier = Modifier.weight(1f))
                    Switch(
                        checked = state.isRangeTime,
                        onCheckedChange = viewModel::onRangeTimeToggle,
                    )
                }
                if (state.isRangeTime) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.timeEnd?.format(timeFormatter) ?: "Конец не задан",
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = {
                            timeTargetTo = true
                            showTimePicker = true
                        }) { Text("Выбрать") }
                    }
                }
            }

            // ── Повтор ──
            Section(title = "Повтор") {
                DraftRecurrencePreset.entries.forEach { preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = state.recurrence == preset,
                                onClick = { viewModel.onRecurrenceChange(preset) },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = state.recurrence == preset,
                            onClick = { viewModel.onRecurrenceChange(preset) },
                        )
                        Text(preset.displayName())
                    }
                }
            }

            // ── Нотификации ──
            Section(title = "Нотификации") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    reminderPresets.forEach { minutes ->
                        val selected = state.reminders.any { it.minutesBefore == minutes }
                        FilterChip(
                            selected = selected,
                            onClick = {
                                val next = if (selected) {
                                    state.reminders.filterNot { it.minutesBefore == minutes }
                                } else {
                                    state.reminders + DraftReminder(minutes)
                                }
                                viewModel.onRemindersChange(next)
                            },
                            label = { Text(formatReminder(minutes)) },
                        )
                    }
                }
            }

            // ── Будильники ──
            Section(title = "Будильники") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (state.alarms.isEmpty()) "Нет" else "${state.alarms.size} шт.",
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { showAlarmPicker = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                }
                state.alarms.forEach { alarm ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = alarm.time.format(timeFormatter),
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = {
                            viewModel.onAlarmsChange(state.alarms - alarm)
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Удалить")
                        }
                    }
                }
            }

            // ── ОК ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = {
                    viewModel.onApply()
                    onDismiss()
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Применить")
                }
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = (if (dateTargetTo) state.dateTo else state.dateFrom)
            ?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = pickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    if (dateTargetTo) viewModel.onDateToChange(date)
                    else viewModel.onDateFromChange(date)
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
        val initial = if (timeTargetTo) state.timeEnd else state.timeFrom
        val pickerState = rememberTimePickerState(
            initialHour = initial?.hour ?: 0,
            initialMinute = initial?.minute ?: 0,
        )
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(pickerState.hour, pickerState.minute)
                    if (timeTargetTo) viewModel.onTimeEndChange(time)
                    else viewModel.onTimeFromChange(time)
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

    if (showAlarmPicker) {
        val pickerState = rememberTimePickerState(initialHour = 9, initialMinute = 0)
        DatePickerDialog(
            onDismissRequest = { showAlarmPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = LocalTime.of(pickerState.hour, pickerState.minute)
                    viewModel.onAlarmsChange(state.alarms + DraftAlarm(time))
                    showAlarmPicker = false
                }) { Text("ОК") }
            },
            dismissButton = {
                TextButton(onClick = { showAlarmPicker = false }) { Text("Отмена") }
            },
        ) {
            TimePicker(state = pickerState)
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        content()
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