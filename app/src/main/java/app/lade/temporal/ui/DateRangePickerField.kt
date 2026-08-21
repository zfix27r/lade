package app.lade.temporal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerField(
	dateFrom: LocalDate,
	dateTo: LocalDate,
	onDateFromChange: (LocalDate) -> Unit,
	onDateToChange: (LocalDate) -> Unit,
	modifier: Modifier = Modifier,
) {
	var pickingFrom by remember { mutableStateOf(false) }
	var pickingTo by remember { mutableStateOf(false) }
	val formatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
	val gap = dimensionResource(R.dimen.spacing_sm)

	Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(gap),
		) {
			OutlinedButton(
				onClick = { pickingFrom = true },
				modifier = Modifier.weight(1f),
			) {
				Text(stringResource(R.string.format_date_from, dateFrom.format(formatter)))
			}
			OutlinedButton(
				onClick = { pickingTo = true },
				modifier = Modifier.weight(1f),
			) {
				Text(stringResource(R.string.format_date_to, dateTo.format(formatter)))
			}
		}
	}

	if (pickingFrom) {
		val state = rememberDatePickerState(
			initialSelectedDateMillis = dateFrom.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
		)
		DatePickerDialog(
			onDismissRequest = { pickingFrom = false },
			confirmButton = {
				TextButton(
					onClick = {
						state.selectedDateMillis?.let { millis ->
							onDateFromChange(
								Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate(),
							)
						}
						pickingFrom = false
					},
				) { Text(stringResource(R.string.action_ok)) }
			},
			dismissButton = {
				TextButton(onClick = { pickingFrom = false }) {
					Text(stringResource(R.string.action_cancel))
				}
			},
		) {
			DatePicker(state = state)
		}
	}

	if (pickingTo) {
		val state = rememberDatePickerState(
			initialSelectedDateMillis = dateTo.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
		)
		DatePickerDialog(
			onDismissRequest = { pickingTo = false },
			confirmButton = {
				TextButton(
					onClick = {
						state.selectedDateMillis?.let { millis ->
							onDateToChange(
								Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate(),
							)
						}
						pickingTo = false
					},
				) { Text(stringResource(R.string.action_ok)) }
			},
			dismissButton = {
				TextButton(onClick = { pickingTo = false }) {
					Text(stringResource(R.string.action_cancel))
				}
			},
		) {
			DatePicker(state = state)
		}
	}
}
