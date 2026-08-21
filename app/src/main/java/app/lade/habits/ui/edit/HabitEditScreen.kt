package app.lade.habits.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.R
import app.lade.categories.ui.CategorySelector
import app.lade.habits.domain.model.HabitGoalPresets
import app.lade.habits.ui.habitUnitLabel
import app.lade.temporal.ui.TemporalOptionsConfig
import app.lade.temporal.ui.TemporalOptionsPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitEditScreen(
	onBack: () -> Unit,
	viewModel: HabitEditViewModel = hiltViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val categories by viewModel.categories.collectAsStateWithLifecycle()
	LaunchedEffect(state.saved) {
		if (state.saved) onBack()
	}

	var unitExpanded by remember { mutableStateOf(false) }
	val canSave = state.title.isNotBlank() &&
		state.categoryId != null &&
		state.temporal.recurrence.hasValidDays()

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						stringResource(
							if (state.isNew) R.string.habit_new else R.string.habit_edit,
						),
					)
				},
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
			OutlinedTextField(
				value = state.title,
				onValueChange = viewModel::onTitleChange,
				label = { Text(stringResource(R.string.habit_field_title)) },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true,
			)
			CategorySelector(
				categories = categories,
				selectedId = state.categoryId,
				onSelected = viewModel::onCategoryChange,
			)
			OutlinedTextField(
				value = state.goalValueText,
				onValueChange = viewModel::onGoalValueChange,
				label = { Text(stringResource(R.string.habit_field_goal)) },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true,
			)
			ExposedDropdownMenuBox(
				expanded = unitExpanded,
				onExpandedChange = { unitExpanded = it },
			) {
				OutlinedTextField(
					value = habitUnitLabel(state.goalUnit),
					onValueChange = {},
					readOnly = true,
					label = { Text(stringResource(R.string.habit_field_unit)) },
					trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
					modifier = Modifier
						.menuAnchor(MenuAnchorType.PrimaryNotEditable)
						.fillMaxWidth(),
				)
				ExposedDropdownMenu(
					expanded = unitExpanded,
					onDismissRequest = { unitExpanded = false },
				) {
					HabitGoalPresets.UNIT_CODES.forEach { unit ->
						DropdownMenuItem(
							text = { Text(habitUnitLabel(unit)) },
							onClick = {
								viewModel.onGoalUnitChange(unit)
								unitExpanded = false
							},
						)
					}
				}
			}
			TemporalOptionsPanel(
				value = state.temporal,
				onChange = viewModel::onTemporalChange,
				config = TemporalOptionsConfig(
					showTime = true,
					showRecurrence = true,
					showAlarm = true,
					showReminder = true,
				),
			)
			Button(
				onClick = viewModel::save,
				modifier = Modifier.fillMaxWidth(),
				enabled = canSave,
			) {
				Text(stringResource(R.string.action_save))
			}
		}
	}
}
