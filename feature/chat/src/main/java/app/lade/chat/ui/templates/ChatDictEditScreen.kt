package app.lade.chat.ui.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import app.lade.entrykind.EntryKind
import app.lade.resources.R
import app.lade.ui.components.inputs.TitleTextField
import app.lade.ui.components.layout.EditSectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDictEditScreen(
	onBack: () -> Unit,
	viewModel: ChatDictEditViewModel = hiltViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	LaunchedEffect(state.saved) {
		if (state.saved) onBack()
	}
	val canSave = state.title.isNotBlank() && state.phrasesText.isNotBlank()

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						stringResource(
							if (state.isNew) R.string.chat_dict_new else R.string.chat_dict_edit,
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
			EditSectionCard(title = stringResource(R.string.edit_section_title)) {
				TitleTextField(
					value = state.title,
					onValueChange = viewModel::onTitleChange,
				)
			}
			EditSectionCard(title = stringResource(R.string.chat_dict_field_kind)) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
				) {
					EntryKind.entries.forEach { kind ->
						FilterChip(
							selected = state.kind == kind,
							onClick = { viewModel.onKindChange(kind) },
							label = { Text(kind.storage) },
						)
					}
				}
			}
			EditSectionCard(title = stringResource(R.string.chat_dict_field_phrases)) {
				OutlinedTextField(
					value = state.phrasesText,
					onValueChange = viewModel::onPhrasesChange,
					label = { Text(stringResource(R.string.chat_dict_field_phrases_hint)) },
					modifier = Modifier.fillMaxWidth(),
				)
			}
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
