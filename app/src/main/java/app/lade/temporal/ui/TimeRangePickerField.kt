package app.lade.temporal.ui

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
import app.lade.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangePickerField(
	start: LocalTime,
	end: LocalTime,
	onStartChange: (LocalTime) -> Unit,
	onEndChange: (LocalTime) -> Unit,
	modifier: Modifier = Modifier,
) {
	var pickingStart by remember { mutableStateOf(false) }
	var pickingEnd by remember { mutableStateOf(false) }
	val timePattern = stringResource(R.string.format_time_hm)
	val formatter = remember(timePattern) { DateTimeFormatter.ofPattern(timePattern) }
	val gap = dimensionResource(R.dimen.spacing_sm)

	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(gap),
	) {
		OutlinedButton(
			onClick = { pickingStart = true },
			modifier = Modifier.weight(1f),
		) {
			Text(stringResource(R.string.format_time_from, start.format(formatter)))
		}
		OutlinedButton(
			onClick = { pickingEnd = true },
			modifier = Modifier.weight(1f),
		) {
			Text(stringResource(R.string.format_time_to, end.format(formatter)))
		}
	}

	if (pickingStart) {
		val state = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = true)
		AlertDialog(
			onDismissRequest = { pickingStart = false },
			confirmButton = {
				TextButton(
					onClick = {
						onStartChange(LocalTime.of(state.hour, state.minute))
						pickingStart = false
					},
				) { Text(stringResource(R.string.action_ok)) }
			},
			dismissButton = {
				TextButton(onClick = { pickingStart = false }) {
					Text(stringResource(R.string.action_cancel))
				}
			},
			text = { TimePicker(state = state) },
		)
	}

	if (pickingEnd) {
		val state = rememberTimePickerState(initialHour = end.hour, initialMinute = end.minute, is24Hour = true)
		AlertDialog(
			onDismissRequest = { pickingEnd = false },
			confirmButton = {
				TextButton(
					onClick = {
						onEndChange(LocalTime.of(state.hour, state.minute))
						pickingEnd = false
					},
				) { Text(stringResource(R.string.action_ok)) }
			},
			dismissButton = {
				TextButton(onClick = { pickingEnd = false }) {
					Text(stringResource(R.string.action_cancel))
				}
			},
			text = { TimePicker(state = state) },
		)
	}
}
