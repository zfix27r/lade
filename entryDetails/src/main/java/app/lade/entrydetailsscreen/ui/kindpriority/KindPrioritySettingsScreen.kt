package app.lade.entrydetailsscreen.ui.kindpriority

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.entry.ui.entryKindLabel
import app.lade.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KindPrioritySettingsScreen(
	onBack: () -> Unit,
	viewModel: KindPrioritySettingsViewModel = hiltViewModel(),
) {
	val order by viewModel.order.collectAsStateWithLifecycle()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.kind_priority_title)) },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(R.string.action_back),
						)
					}
				},
				actions = {
					TextButton(onClick = viewModel::resetDefault) {
						Text(stringResource(R.string.action_clear))
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
			verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
		) {
			Text(
				text = stringResource(R.string.kind_priority_hint),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			order.forEachIndexed { index, kind ->
				ListItem(
					headlineContent = { Text(entryKindLabel(kind)) },
					supportingContent = {
						Text(
							stringResource(
								R.string.kind_priority_rank,
								index + 1,
							),
						)
					},
					trailingContent = {
						Row(verticalAlignment = Alignment.CenterVertically) {
							IconButton(
								onClick = { viewModel.moveUp(index) },
								enabled = index > 0,
							) {
								Icon(
									Icons.Default.KeyboardArrowUp,
									contentDescription = stringResource(R.string.kind_priority_move_up),
								)
							}
							IconButton(
								onClick = { viewModel.moveDown(index) },
								enabled = index < order.lastIndex,
							) {
								Icon(
									Icons.Default.KeyboardArrowDown,
									contentDescription = stringResource(R.string.kind_priority_move_down),
								)
							}
						}
					},
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}
	}
}
