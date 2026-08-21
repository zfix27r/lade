package app.lade.temporal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.R
import app.lade.temporal.domain.RecurrenceDraft
import app.lade.temporal.domain.RecurrencePreset
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemporalOptionsPanel(
	value: TemporalOptions,
	onChange: (TemporalOptions) -> Unit,
	config: TemporalOptionsConfig,
	modifier: Modifier = Modifier,
) {
	val timePattern = stringResource(R.string.format_time_hm)
	val timeFmt = remember(timePattern) { DateTimeFormatter.ofPattern(timePattern) }
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
	) {
		if (config.showDate) {
			SectionTitle(stringResource(R.string.temporal_section_date))
			SingleDateButton(
				label = value.date?.toString() ?: stringResource(R.string.temporal_pick_date),
				date = value.date ?: LocalDate.now(),
				onPicked = { onChange(value.copy(date = it)) },
			)
		}
		if (config.showDateRange) {
			SectionTitle(stringResource(R.string.temporal_section_date_range))
			DateRangePickerField(
				dateFrom = value.dateFrom ?: LocalDate.now(),
				dateTo = value.dateTo ?: LocalDate.now().plusMonths(1),
				onDateFromChange = { onChange(value.copy(dateFrom = it)) },
				onDateToChange = { onChange(value.copy(dateTo = it)) },
			)
		}
		if (config.showTime) {
			SectionTitle(stringResource(R.string.temporal_section_time))
			val timeText = value.time?.format(timeFmt)
				?: stringResource(R.string.temporal_time_unset)
			SingleTimeButton(
				label = stringResource(R.string.format_time_optional, timeText),
				time = value.time ?: LocalTime.of(9, 0),
				onPicked = { onChange(value.copy(time = it)) },
				onClear = { onChange(value.copy(time = null)) },
				clearable = true,
			)
		}
		if (config.showTimeRange) {
			SectionTitle(stringResource(R.string.temporal_section_time_range))
			TimeRangePickerField(
				start = value.time ?: LocalTime.of(9, 0),
				end = value.timeEnd ?: LocalTime.of(18, 0),
				onStartChange = { onChange(value.copy(time = it)) },
				onEndChange = { onChange(value.copy(timeEnd = it)) },
			)
		}
		if (config.showRecurrence) {
			SectionTitle(stringResource(R.string.temporal_section_recurrence))
			RecurrenceSection(
				draft = value.recurrence,
				presets = config.recurrencePresets,
				onDraftChange = { onChange(value.copy(recurrence = it)) },
			)
		}
		if (config.showAlarm) {
			SectionTitle(stringResource(R.string.temporal_section_alarm))
			AlarmDropdown(
				selected = value.alarmMode,
				onSelected = { onChange(value.copy(alarmMode = it)) },
			)
		}
		if (config.showReminder) {
			SectionTitle(stringResource(R.string.temporal_section_reminder))
			ReminderDropdown(
				minutes = value.reminderMinutesBefore,
				onChange = { onChange(value.copy(reminderMinutesBefore = it)) },
			)
		}
	}
}

@Composable
private fun SectionTitle(text: String) {
	Text(text = text, style = MaterialTheme.typography.titleSmall)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurrenceSection(
	draft: RecurrenceDraft,
	presets: List<RecurrencePreset>,
	onDraftChange: (RecurrenceDraft) -> Unit,
) {
	var presetExpanded by remember { mutableStateOf(false) }
	ExposedDropdownMenuBox(
		expanded = presetExpanded,
		onExpandedChange = { presetExpanded = it },
	) {
		OutlinedTextField(
			value = draft.preset.label(),
			onValueChange = {},
			readOnly = true,
			label = { Text(stringResource(R.string.temporal_field_type)) },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = presetExpanded) },
			modifier = Modifier
				.menuAnchor(MenuAnchorType.PrimaryNotEditable)
				.fillMaxWidth(),
		)
		ExposedDropdownMenu(
			expanded = presetExpanded,
			onDismissRequest = { presetExpanded = false },
		) {
			presets.forEach { preset ->
				DropdownMenuItem(
					text = { Text(preset.label()) },
					onClick = {
						onDraftChange(draft.copy(preset = preset))
						presetExpanded = false
					},
				)
			}
		}
	}
	if (draft.preset == RecurrencePreset.EveryNDays ||
		draft.preset == RecurrencePreset.Weekly ||
		draft.preset == RecurrencePreset.Monthly ||
		draft.preset == RecurrencePreset.Yearly
	) {
		OutlinedTextField(
			value = draft.interval.toString(),
			onValueChange = { text ->
				val n = text.filter { it.isDigit() }.toIntOrNull()?.coerceAtLeast(1) ?: 1
				onDraftChange(draft.copy(interval = n))
			},
			label = { Text(stringResource(R.string.temporal_field_interval)) },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
		)
	}
	if (draft.preset == RecurrencePreset.Weekly) {
		Text(
			stringResource(R.string.temporal_field_days),
			style = MaterialTheme.typography.bodyMedium,
		)
		DaysOfWeekSelector(
			flags = draft.daysOfWeek,
			onFlagsChange = { onDraftChange(draft.copy(daysOfWeek = it)) },
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmDropdown(
	selected: AlarmModeOption,
	onSelected: (AlarmModeOption) -> Unit,
) {
	var expanded by remember { mutableStateOf(false) }
	ExposedDropdownMenuBox(
		expanded = expanded,
		onExpandedChange = { expanded = it },
	) {
		OutlinedTextField(
			value = selected.label(),
			onValueChange = {},
			readOnly = true,
			label = { Text(stringResource(R.string.temporal_field_mode)) },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
			modifier = Modifier
				.menuAnchor(MenuAnchorType.PrimaryNotEditable)
				.fillMaxWidth(),
		)
		ExposedDropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false },
		) {
			AlarmModeOption.entries.forEach { mode ->
				DropdownMenuItem(
					text = { Text(mode.label()) },
					onClick = {
						onSelected(mode)
						expanded = false
					},
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderDropdown(
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
				.menuAnchor(MenuAnchorType.PrimaryNotEditable)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleDateButton(
	label: String,
	date: LocalDate,
	onPicked: (LocalDate) -> Unit,
) {
	var open by remember { mutableStateOf(false) }
	OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
		Text(label)
	}
	if (open) {
		val state = rememberDatePickerState(
			initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
		)
		DatePickerDialog(
			onDismissRequest = { open = false },
			confirmButton = {
				TextButton(
					onClick = {
						state.selectedDateMillis?.let { millis ->
							onPicked(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
						}
						open = false
					},
				) { Text(stringResource(R.string.action_ok)) }
			},
			dismissButton = {
				TextButton(onClick = { open = false }) {
					Text(stringResource(R.string.action_cancel))
				}
			},
		) {
			DatePicker(state = state)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleTimeButton(
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
