package app.lade.time.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.R
import app.lade.temporal.domain.RecurrenceDraft
import app.lade.temporal.ui.label
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSchedulesListScreen(
	onBack: (() -> Unit)? = null,
	onAdd: () -> Unit,
	onEdit: (Long) -> Unit,
	viewModel: TimeSchedulesListViewModel = hiltViewModel(),
) {
	val schedules by viewModel.schedules.collectAsStateWithLifecycle()
	val dateFmt = DateTimeFormatter.ISO_LOCAL_DATE
	val timePattern = stringResource(R.string.format_time_hm)
	val timeFmt = remember(timePattern) { DateTimeFormatter.ofPattern(timePattern) }
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.time_schedules_title)) },
				navigationIcon = {
					if (onBack != null) {
						IconButton(onClick = onBack) {
							Icon(
								Icons.AutoMirrored.Filled.ArrowBack,
								contentDescription = stringResource(R.string.action_back),
							)
						}
					}
				},
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = onAdd) {
				Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
			}
		},
	) { padding ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
		) {
			items(schedules, key = { it.id }) { schedule ->
				val recurrence = RecurrenceDraft.fromRrule(schedule.rrule).label()
				ListItem(
					headlineContent = { Text(schedule.title) },
					supportingContent = {
						Text(
							stringResource(
								R.string.format_schedule_supporting,
								schedule.dateFrom.format(dateFmt),
								schedule.dateTo.format(dateFmt),
								schedule.startTime.format(timeFmt),
								schedule.endTime.format(timeFmt),
								recurrence,
							),
						)
					},
					trailingContent = {
						IconButton(onClick = { viewModel.delete(schedule.id) }) {
							Icon(
								Icons.Default.Delete,
								contentDescription = stringResource(R.string.action_delete),
							)
						}
					},
					modifier = Modifier
						.fillMaxWidth()
						.clickable { onEdit(schedule.id) },
				)
			}
		}
	}
}
