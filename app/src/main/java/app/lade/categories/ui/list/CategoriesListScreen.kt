package app.lade.categories.ui.list

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesListScreen(
	onBack: () -> Unit,
	onAdd: () -> Unit,
	onEdit: (Long) -> Unit,
	viewModel: CategoriesListViewModel = hiltViewModel(),
) {
	val categories by viewModel.categories.collectAsStateWithLifecycle()
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.categories_title)) },
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
			items(categories, key = { it.id }) { category ->
				ListItem(
					headlineContent = { Text(category.title) },
					supportingContent = {
						Text(category.key ?: stringResource(R.string.category_key_custom))
					},
					trailingContent = {
						IconButton(onClick = { viewModel.archive(category.id) }) {
							Icon(
								Icons.Default.Delete,
								contentDescription = stringResource(R.string.action_archive),
							)
						}
					},
					modifier = Modifier
						.fillMaxWidth()
						.clickable { onEdit(category.id) },
				)
			}
		}
	}
}
