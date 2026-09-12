package app.lade.chat.ui.templates

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTemplatesListScreen(
	onBack: () -> Unit,
	onAddDict: () -> Unit,
	onEditDict: (Long) -> Unit,
	viewModel: ChatTemplatesListViewModel = hiltViewModel(),
) {
	val systemCorpus by viewModel.systemCorpus.collectAsStateWithLifecycle()
	val userDicts by viewModel.userDicts.collectAsStateWithLifecycle()
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.chat_templates_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.action_back),
						)
					}
				},
				actions = {
					TextButton(onClick = viewModel::clearChatHistory) {
						Text(stringResource(R.string.chat_clear_history))
					}
				},
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = onAddDict) {
				Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
			}
		},
	) { padding ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
		) {
			item {
				SectionHeader(stringResource(R.string.chat_section_corpus))
			}
			item {
				TextButton(
					onClick = viewModel::reloadSystemCorpus,
					modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.spacing_md)),
				) {
					Text(stringResource(R.string.chat_reload_corpus))
				}
			}
			items(systemCorpus, key = { "c-${it.systemKey}" }) { entry ->
				ListItem(
					headlineContent = {
						Text("${entry.title} · ${stringResource(R.string.chat_system_badge)}")
					},
					supportingContent = {
						Text(
							entry.kind.storage + " · " + entry.needles.joinToString { it.text },
						)
					},
				)
			}
			item {
				SectionHeader(stringResource(R.string.chat_section_dicts))
			}
			item {
				TextButton(
					onClick = onAddDict,
					modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.spacing_md)),
				) {
					Text(stringResource(R.string.chat_dict_add))
				}
			}
			items(userDicts, key = { "d-${it.id}" }) { dict ->
				ListItem(
					headlineContent = { Text(dict.title) },
					supportingContent = {
						Text("${dict.kind.storage} · ${dict.phrases.joinToString()}")
					},
					trailingContent = {
						IconButton(onClick = { viewModel.archiveDict(dict.id) }) {
							Icon(
								Icons.Default.Delete,
								contentDescription = stringResource(R.string.action_archive),
							)
						}
					},
					modifier = Modifier
						.fillMaxWidth()
						.clickable { onEditDict(dict.id) }
						.padding(horizontal = dimensionResource(R.dimen.spacing_sm)),
				)
			}
		}
	}
}

@Composable
private fun SectionHeader(text: String) {
	Text(
		text = text,
		style = MaterialTheme.typography.titleSmall,
		color = MaterialTheme.colorScheme.primary,
		modifier = Modifier.padding(
			horizontal = dimensionResource(R.dimen.screen_padding),
			vertical = dimensionResource(R.dimen.spacing_sm),
		),
	)
}
