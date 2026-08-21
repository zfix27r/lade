package app.lade.calendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDayNav(
	date: LocalDate,
	onPrevious: () -> Unit,
	onNext: () -> Unit,
	onToday: () -> Unit,
	onDateSelected: (LocalDate) -> Unit,
	modifier: Modifier = Modifier,
) {
	var picking by remember { mutableStateOf(false) }
	val dateLabel = remember(date) {
		DateTimeFormatter
			.ofLocalizedDate(FormatStyle.MEDIUM)
			.withLocale(Locale.getDefault())
			.format(date)
	}

	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = dimensionResource(R.dimen.spacing_sm)),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		IconButton(onClick = onPrevious) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowLeft,
				contentDescription = stringResource(R.string.calendar_prev_day),
			)
		}
		Row(verticalAlignment = Alignment.CenterVertically) {
			TextButton(onClick = { picking = true }) {
				Text(
					text = dateLabel,
					style = MaterialTheme.typography.titleMedium,
				)
			}
			TextButton(onClick = onToday) {
				Text(stringResource(R.string.calendar_today))
			}
		}
		IconButton(onClick = onNext) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowRight,
				contentDescription = stringResource(R.string.calendar_next_day),
			)
		}
	}

	if (picking) {
		val state = rememberDatePickerState(
			initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
		)
		DatePickerDialog(
			onDismissRequest = { picking = false },
			confirmButton = {
				TextButton(
					onClick = {
						state.selectedDateMillis?.let { millis ->
							onDateSelected(
								Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate(),
							)
						}
						picking = false
					},
				) {
					Text(stringResource(R.string.action_ok))
				}
			},
			dismissButton = {
				TextButton(onClick = { picking = false }) {
					Text(stringResource(R.string.action_cancel))
				}
			},
		) {
			DatePicker(state = state)
		}
	}
}
