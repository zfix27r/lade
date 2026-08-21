package app.lade.time.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.R
import app.lade.categories.ui.CategorySelector
import app.lade.temporal.ui.TemporalOptionsConfig
import app.lade.temporal.ui.TemporalOptionsPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeBlockEditScreen(
	onBack: () -> Unit,
	viewModel: TimeBlockEditViewModel = hiltViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val categories by viewModel.categories.collectAsStateWithLifecycle()
	LaunchedEffect(state.saved, state.deleted) {
		if (state.saved || state.deleted) onBack()
	}

	val t = state.temporal
	val canSave = state.isManual &&
		state.categoryId != null &&
		t.date != null &&
		t.time != null &&
		t.timeEnd != null &&
		t.timeEnd!!.isAfter(t.time)

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						stringResource(
							if (state.isNew) R.string.time_block_new else R.string.time_block_edit,
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
				actions = {
					if (!state.isNew && state.isManual) {
						IconButton(onClick = viewModel::delete) {
							Icon(
								Icons.Default.Delete,
								contentDescription = stringResource(R.string.action_delete),
							)
						}
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
				label = { Text(stringResource(R.string.time_block_field_title)) },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true,
				enabled = state.isManual,
			)
			CategorySelector(
				categories = categories,
				selectedId = state.categoryId,
				onSelected = { id ->
					if (state.isManual) viewModel.onCategoryChange(id)
				},
			)
			TemporalOptionsPanel(
				value = state.temporal,
				onChange = { value ->
					if (state.isManual) viewModel.onTemporalChange(value)
				},
				config = TemporalOptionsConfig(
					showDate = true,
					showTimeRange = true,
				),
			)
			if (state.error) {
				Text(
					text = stringResource(R.string.time_block_error_overlap),
					color = MaterialTheme.colorScheme.error,
					style = MaterialTheme.typography.bodyMedium,
				)
			}
			if (state.isManual) {
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
}
