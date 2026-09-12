package app.lade.categories.ui.edit

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import app.lade.resources.R
import app.lade.categories.ui.CategoryColorPicker
import app.lade.ui.components.layout.EditSectionCard
import app.lade.ui.components.inputs.TitleTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(
	onBack: () -> Unit,
	viewModel: CategoryEditViewModel = hiltViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	LaunchedEffect(state.saved) {
		if (state.saved) onBack()
	}

	Scaffold(
		contentWindowInsets = WindowInsets.safeDrawing.only(
			WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
		),
		topBar = {
			TopAppBar(
				title = {
					Text(
						stringResource(
							if (state.isNew) R.string.category_new else R.string.category_edit,
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
			EditSectionCard(title = stringResource(R.string.category_field_color)) {
				CategoryColorPicker(
					selected = state.color,
					onSelected = viewModel::onColorChange,
				)
			}
			Button(
				onClick = viewModel::save,
				modifier = Modifier.fillMaxWidth(),
				enabled = state.title.isNotBlank(),
			) {
				Text(stringResource(R.string.action_save))
			}
		}
	}
}
