package app.lade.reminders.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.daypart.domain.DayPart
import app.lade.daypart.ui.label
import app.lade.resources.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPartSettingsScreen(
	onBack: () -> Unit,
	viewModel: DayPartSettingsViewModel = hiltViewModel(),
) {
	val clock by viewModel.clock.collectAsStateWithLifecycle()
	var editing by remember { mutableStateOf<DayPart?>(null) }
	val timePattern = stringResource(R.string.format_time_hm)
	val timeFmt = remember(timePattern) { DateTimeFormatter.ofPattern(timePattern) }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.reminders_daypart_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.action_back),
						)
					}
				},
			)
		},
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(dimensionResource(R.dimen.screen_padding))
				.verticalScroll(rememberScrollState()),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
		) {
			Text(
				text = stringResource(R.string.reminders_daypart_subtitle),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			DayPart.entries.forEach { part ->
				OutlinedButton(
					onClick = { editing = part },
					modifier = Modifier.fillMaxWidth(),
				) {
					Text("${part.label()} · ${clock.timeOf(part).format(timeFmt)}")
				}
			}
			Text(
				text = stringResource(R.string.alarm_mode_notification_hint),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Text(
				text = stringResource(R.string.alarm_mode_alarm_hint),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}

	val part = editing
	if (part != null) {
		val current = clock.timeOf(part)
		val state = rememberTimePickerState(
			initialHour = current.hour,
			initialMinute = current.minute,
			is24Hour = true,
		)
		AlertDialog(
			onDismissRequest = { editing = null },
			confirmButton = {
				TextButton(
					onClick = {
						viewModel.setTime(part, LocalTime.of(state.hour, state.minute))
						editing = null
					},
				) { Text(stringResource(R.string.action_ok)) }
			},
			dismissButton = {
				TextButton(onClick = { editing = null }) {
					Text(stringResource(R.string.action_cancel))
				}
			},
			title = { Text(part.label()) },
			text = { TimePicker(state = state) },
		)
	}
}
