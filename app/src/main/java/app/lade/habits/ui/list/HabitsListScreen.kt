package app.lade.habits.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.R
import app.lade.habits.domain.model.HabitHistoryResult
import app.lade.habits.ui.habitUnitLabel
import app.lade.habits.ui.mark.HabitDayMarkActions
import app.lade.habits.ui.mark.HabitHistorySheet
import app.lade.habits.ui.mark.HabitStreakPreviewRow
import app.lade.temporal.domain.RecurrenceDraft
import app.lade.temporal.ui.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsListScreen(
	onBack: (() -> Unit)? = null,
	onAdd: () -> Unit,
	onEdit: (Long) -> Unit,
	viewModel: HabitsListViewModel = hiltViewModel(),
) {
	val items by viewModel.items.collectAsStateWithLifecycle()
	val historySheet by viewModel.historySheet.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	val undoLabel = stringResource(R.string.habit_mark_undo)
	val doneMsg = stringResource(R.string.habit_mark_snackbar_done)
	val skipMsg = stringResource(R.string.habit_mark_snackbar_skipped)

	LaunchedEffect(Unit) {
		viewModel.undoEvents.collect { event ->
			val message = when (event.newResult) {
				HabitHistoryResult.DONE -> doneMsg
				else -> skipMsg
			}
			val result = snackbarHostState.showSnackbar(
				message = message,
				actionLabel = undoLabel,
			)
			if (result == SnackbarResult.ActionPerformed) {
				viewModel.undo(event)
			}
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.habits_title)) },
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
		snackbarHost = { SnackbarHost(snackbarHostState) },
		floatingActionButton = {
			FloatingActionButton(onClick = onAdd) {
				Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
			}
		},
	) { padding ->
		if (items.isEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding),
				contentAlignment = Alignment.Center,
			) {
				Text(
					text = stringResource(R.string.habits_empty),
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding),
			) {
				items(items, key = { it.habit.id }) { item ->
					val habit = item.habit
					val recurrence = RecurrenceDraft.fromRrule(habit.rrule).label()
					val unit = habitUnitLabel(habit.goalUnit)
					ListItem(
						headlineContent = { Text(habit.title) },
						supportingContent = {
							Column {
								Text(
									stringResource(
										R.string.format_habit_supporting,
										habit.goalValue,
										unit,
										recurrence,
									),
								)
								HabitStreakPreviewRow(
									preview = item.streak,
									modifier = Modifier.padding(
										top = dimensionResource(R.dimen.spacing_xs),
									),
								)
								if (!item.dueToday) {
									Text(
										text = stringResource(R.string.habit_not_due_today),
										style = MaterialTheme.typography.labelSmall,
										color = MaterialTheme.colorScheme.onSurfaceVariant,
									)
								}
							}
						},
						trailingContent = {
							Row(verticalAlignment = Alignment.CenterVertically) {
								if (item.dueToday) {
									HabitDayMarkActions(
										result = item.todayResult,
										onDone = { viewModel.markDone(habit) },
										onSkip = { viewModel.markSkip(habit) },
									)
								}
								IconButton(onClick = { viewModel.openHistory(habit) }) {
									Icon(
										Icons.Default.CalendarMonth,
										contentDescription = stringResource(R.string.habit_history_open),
									)
								}
								IconButton(onClick = { viewModel.delete(habit.id) }) {
									Icon(
										Icons.Default.Delete,
										contentDescription = stringResource(R.string.action_delete),
									)
								}
							}
						},
						modifier = Modifier
							.fillMaxWidth()
							.clickable { onEdit(habit.id) },
					)
				}
			}
		}
	}

	historySheet?.let { sheet ->
		HabitHistorySheet(
			habitTitle = sheet.habit.title,
			month = sheet.month,
			marksByEpochDay = sheet.marksByEpochDay,
			onDismiss = viewModel::dismissHistory,
		)
	}
}
