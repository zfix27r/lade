package app.lade.entrydetailsscreen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.resources.R
import app.lade.categories.ui.CategorySelector
import app.lade.entrydetailsscreen.domain.ContainmentChoice
import app.lade.entrydetailsscreen.domain.EntryGoalPresets
import app.lade.entrydetailsscreen.domain.EntryKind
import app.lade.entrydetailsscreen.domain.EntryMissingField
import app.lade.entrydetailsscreen.domain.unit.entryUnitLabel
import app.lade.temporal.ui.TemporalOptionsPanel
import app.lade.ui.components.layout.EditSectionCard
import app.lade.ui.components.inputs.TitleTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryEditScreen(
	onBack: () -> Unit,
	viewModel: EntryEditViewModel = hiltViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val categories by viewModel.categories.collectAsStateWithLifecycle()
	LaunchedEffect(state.saved) {
		if (state.saved) onBack()
	}

	var unitExpanded by remember { mutableStateOf(false) }
	var missingTitleDraft by remember { mutableStateOf("") }

	Scaffold(
		contentWindowInsets = WindowInsets.safeDrawing.only(
			WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
		),
		topBar = {
			TopAppBar(
				title = {
					Text(
						stringResource(
							if (state.isNew) {
								when (state.kind) {
									EntryKind.TASK -> R.string.entry_new_task
									EntryKind.EVENT -> R.string.entry_new_event
									EntryKind.HABIT -> R.string.entry_new_habit
									EntryKind.SCHEDULE -> R.string.entry_new_schedule
								}
							} else {
								R.string.entry_edit
							},
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
				.imePadding()
				.padding(dimensionResource(R.dimen.screen_padding))
				.verticalScroll(rememberScrollState()),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
		) {
			EditSectionCard(title = stringResource(R.string.edit_section_title)) {
				TitleTextField(
					value = state.title,
					onValueChange = viewModel::onTitleChange,
				)
			}
			EditSectionCard(title = stringResource(R.string.edit_section_category)) {
				CategorySelector(
					categories = categories,
					selectedId = state.categoryId,
					onSelected = viewModel::onCategoryChange,
					onCreateCategory = viewModel::createCategory,
				)
			}
			if (state.kind == EntryKind.HABIT) {
				EditSectionCard(title = stringResource(R.string.edit_section_goal)) {
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
							value = entryUnitLabel(state.goalUnit),
							onValueChange = {},
							readOnly = true,
							label = { Text(stringResource(R.string.habit_field_unit)) },
							trailingIcon = {
								ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
							},
							modifier = Modifier
								.menuAnchor(MenuAnchorType.PrimaryNotEditable)
								.fillMaxWidth(),
						)
						ExposedDropdownMenu(
							expanded = unitExpanded,
							onDismissRequest = { unitExpanded = false },
						) {
							EntryGoalPresets.UNIT_CODES.forEach { unit ->
								DropdownMenuItem(
									text = { Text(entryUnitLabel(unit)) },
									onClick = {
										viewModel.onGoalUnitChange(unit)
										unitExpanded = false
									},
								)
							}
						}
					}
				}
			}
			TemporalOptionsPanel(
				value = state.temporal,
				onChange = viewModel::onTemporalChange,
				config = viewModel.temporalConfig(),
			)
			Button(
				onClick = viewModel::save,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(stringResource(R.string.action_save))
			}
		}
	}

	when (val missing = state.missingPrompt) {
		EntryMissingField.TITLE -> {
			LaunchedEffect(missing) { missingTitleDraft = state.title }
			AlertDialog(
				onDismissRequest = viewModel::dismissMissingPrompt,
				title = { Text(stringResource(R.string.entry_missing_title_prompt)) },
				text = {
					OutlinedTextField(
						value = missingTitleDraft,
						onValueChange = { missingTitleDraft = it },
						modifier = Modifier.fillMaxWidth(),
						singleLine = true,
						keyboardOptions = KeyboardOptions(
							capitalization = KeyboardCapitalization.Sentences,
						),
					)
				},
				confirmButton = {
					TextButton(
						onClick = { viewModel.applyMissingTitle(missingTitleDraft) },
						enabled = missingTitleDraft.isNotBlank(),
					) {
						Text(stringResource(R.string.action_ok))
					}
				},
				dismissButton = {
					TextButton(onClick = viewModel::dismissMissingPrompt) {
						Text(stringResource(R.string.action_cancel))
					}
				},
			)
		}
		EntryMissingField.CATEGORY,
		EntryMissingField.DATE,
		EntryMissingField.TIME_RANGE,
		EntryMissingField.RECURRENCE,
		-> {
			AlertDialog(
				onDismissRequest = viewModel::dismissMissingPrompt,
				title = { Text(stringResource(R.string.entry_missing_field_title)) },
				text = {
					Text(
						stringResource(
							when (missing) {
								EntryMissingField.CATEGORY -> R.string.entry_missing_category
								EntryMissingField.DATE -> R.string.entry_missing_date
								EntryMissingField.TIME_RANGE -> R.string.entry_missing_time
								EntryMissingField.RECURRENCE -> R.string.entry_missing_recurrence
								else -> R.string.entry_missing_field_title
							},
						),
						style = MaterialTheme.typography.bodyMedium,
					)
				},
				confirmButton = {
					TextButton(onClick = viewModel::dismissMissingPrompt) {
						Text(stringResource(R.string.action_ok))
					}
				},
			)
		}
		null -> Unit
	}

	state.containment?.let { conflict ->
		AlertDialog(
			onDismissRequest = viewModel::dismissContainment,
			title = { Text(stringResource(R.string.entry_overlap_title)) },
			text = {
				Text(
					stringResource(
						R.string.entry_overlap_message,
						conflict.covered.title.ifBlank {
							stringResource(R.string.entry_overlap_unnamed)
						},
					),
				)
			},
			confirmButton = {
				TextButton(
					onClick = {
						viewModel.resolveContainmentChoice(ContainmentChoice.DELETE_COVERED)
					},
				) {
					Text(stringResource(R.string.entry_overlap_delete_covered))
				}
			},
			dismissButton = {
				Column {
					TextButton(
						onClick = {
							viewModel.resolveContainmentChoice(ContainmentChoice.SPLIT_COVERING)
						},
					) {
						Text(stringResource(R.string.entry_overlap_split_covering))
					}
					TextButton(onClick = viewModel::dismissContainment) {
						Text(stringResource(R.string.action_cancel))
					}
				}
			},
		)
	}
}
