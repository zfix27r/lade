package app.lade.categories.ui.edit

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
import app.lade.categories.domain.model.CategoryColors

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

	var colorExpanded by remember { mutableStateOf(false) }

	Scaffold(
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
				.padding(dimensionResource(R.dimen.screen_padding))
				.verticalScroll(rememberScrollState()),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
		) {
			OutlinedTextField(
				value = state.title,
				onValueChange = viewModel::onTitleChange,
				label = { Text(stringResource(R.string.category_field_title)) },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true,
			)
			ExposedDropdownMenuBox(
				expanded = colorExpanded,
				onExpandedChange = { colorExpanded = it },
			) {
				OutlinedTextField(
					value = state.color,
					onValueChange = {},
					readOnly = true,
					label = { Text(stringResource(R.string.category_field_color)) },
					trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) },
					modifier = Modifier
						.menuAnchor(MenuAnchorType.PrimaryNotEditable)
						.fillMaxWidth(),
				)
				ExposedDropdownMenu(
					expanded = colorExpanded,
					onDismissRequest = { colorExpanded = false },
				) {
					CategoryColors.ALL.forEach { color ->
						DropdownMenuItem(
							text = { Text(color) },
							onClick = {
								viewModel.onColorChange(color)
								colorExpanded = false
							},
						)
					}
				}
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
