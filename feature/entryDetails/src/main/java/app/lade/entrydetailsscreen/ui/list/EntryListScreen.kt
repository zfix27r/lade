package app.lade.entrydetailsscreen.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.agenda.api.entry.EntryKind
import app.lade.resources.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EntryListScreen(
	onBack: (() -> Unit)? = null,
	onAdd: (EntryKind) -> Unit,
	onEdit: (Long, EntryKind) -> Unit,
	viewModel: EntryListViewModel = hiltViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	var addMenuExpanded by remember { mutableStateOf(false) }

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(stringResource(R.string.entries_title_count, state.items.size))
				},
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
			Box {
				FloatingActionButton(onClick = { addMenuExpanded = true }) {
					Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
				}
				DropdownMenu(
					expanded = addMenuExpanded,
					onDismissRequest = { addMenuExpanded = false },
				) {
					EntryKind.entries.forEach { kind ->
						DropdownMenuItem(
							text = { Text(kind.storage) },
							onClick = {
								addMenuExpanded = false
								onAdd(kind)
							},
						)
					}
				}
			}
		},
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
		) {
			FlowRow(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
			) {
				FilterChip(
					selected = state.kindFilter == null,
					onClick = { viewModel.setKindFilter(null) },
					label = { Text(stringResource(R.string.entry_filter_all)) },
				)
				EntryKind.entries.forEach { kind ->
					FilterChip(
						selected = state.kindFilter == kind,
						onClick = { viewModel.setKindFilter(kind) },
						label = { Text(kind.storage) },
					)
				}
				FilterChip(
					selected = state.showArchived,
					onClick = { viewModel.setShowArchived(!state.showArchived) },
					label = { Text(stringResource(R.string.entry_filter_archived)) },
				)
			}
			if (state.items.isEmpty()) {
				Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = stringResource(
							if (state.showArchived) R.string.entries_archived_empty
							else R.string.entries_empty,
						),
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			} else {
				LazyColumn(modifier = Modifier.fillMaxSize()) {
					items(state.items, key = { it.id }) { entry ->
						ListItem(
							headlineContent = { Text(entry.title) },
							supportingContent = { Text(entry.kind.storage) },
							trailingContent = {
								if (entry.isArchived) {
									IconButton(onClick = { viewModel.restore(entry.id) }) {
										Icon(
											Icons.Default.Unarchive,
											contentDescription = stringResource(R.string.entry_restore),
										)
									}
								} else {
									IconButton(onClick = { viewModel.archive(entry.id) }) {
										Icon(
											Icons.Default.Archive,
											contentDescription = stringResource(R.string.action_archive),
										)
									}
								}
							},
							modifier = Modifier
								.fillMaxWidth()
								.clickable { onEdit(entry.id, entry.kind) },
						)
					}
				}
			}
		}
	}
}
