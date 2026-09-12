package app.lade.more.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
	onOpenCalendar: () -> Unit,
	onEntries: () -> Unit,
	onCategories: () -> Unit,
	onDevices: () -> Unit,
	onCalendars: () -> Unit,
	onDayPartSettings: () -> Unit,
	onKindPrioritySettings: () -> Unit,
	viewModel: TodaySummaryViewModel = hiltViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()

	Scaffold(
		topBar = {
			TopAppBar(title = { Text(stringResource(R.string.today_section_title)) })
		},
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.verticalScroll(rememberScrollState())
				.padding(dimensionResource(R.dimen.screen_padding)),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
		) {
			Text(
				text = stringResource(R.string.today_section_title),
				style = MaterialTheme.typography.titleLarge,
			)
			if (state.openCount == 0) {
				Text(
					text = stringResource(R.string.today_entries_empty),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			} else {
				Text(
					text = stringResource(R.string.today_entries_open, state.openCount),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				if (state.dueHabitTotal > 0) {
					Text(
						text = stringResource(
							R.string.today_due_summary,
							state.dueHabitDone,
							state.dueHabitTotal,
						),
						style = MaterialTheme.typography.bodyLarge,
					)
				}
				state.previewTitles.forEach { title ->
					Text(
						text = "· $title",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			}
			Button(
				onClick = onOpenCalendar,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(stringResource(R.string.today_open_calendar))
			}

			Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
			Text(
				text = stringResource(R.string.more_section_lists),
				style = MaterialTheme.typography.titleMedium,
			)
			ListItem(
				headlineContent = { Text(stringResource(R.string.home_entries)) },
				supportingContent = {
					Text(stringResource(R.string.home_entries_subtitle))
				},
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = onEntries),
			)

			Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
			Text(
				text = stringResource(R.string.more_section_links),
				style = MaterialTheme.typography.titleMedium,
			)
			ListItem(
				headlineContent = { Text(stringResource(R.string.home_categories)) },
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = onCategories),
			)
			ListItem(
				headlineContent = { Text(stringResource(R.string.kind_priority_title)) },
				supportingContent = {
					Text(stringResource(R.string.kind_priority_subtitle))
				},
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = onKindPrioritySettings),
			)
			ListItem(
				headlineContent = { Text(stringResource(R.string.home_devices)) },
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = onDevices),
			)
			ListItem(
				headlineContent = { Text(stringResource(R.string.home_calsync)) },
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = onCalendars),
			)
			ListItem(
				headlineContent = { Text(stringResource(R.string.reminders_daypart_title)) },
				supportingContent = {
					Text(stringResource(R.string.reminders_daypart_subtitle))
				},
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = onDayPartSettings),
			)
		}
	}
}
