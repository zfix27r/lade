package app.lade.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.resources.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
	onOpenProfile: () -> Unit,
	onOpenTemplates: () -> Unit,
	onCreateEntry: (kind: String, title: String) -> Unit = { _, _ -> },
	bottomNavHeight: Dp = 0.dp,
	viewModel: ChatViewModel = hiltViewModel(),
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val listState = rememberLazyListState()

	LaunchedEffect(state.bubbles.size, state.choices.size) {
		if (state.bubbles.isNotEmpty()) {
			listState.animateScrollToItem(state.bubbles.lastIndex)
		}
	}

	Scaffold(
		contentWindowInsets = WindowInsets.safeDrawing.only(
			WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
		),
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.nav_chat)) },
				actions = {
					IconButton(onClick = onOpenTemplates) {
						Icon(
							Icons.Default.Settings,
							contentDescription = stringResource(R.string.chat_templates_title),
						)
					}
				},
			)
		},
		bottomBar = {
			ChatInputBar(
				bottomNavHeight = bottomNavHeight,
				input = state.input,
				sending = state.sending,
				choices = state.choices,
				onInputChange = viewModel::onInputChange,
				onSend = viewModel::send,
				onChoice = viewModel::onChoice,
			)
		},
	) { padding ->
		LazyColumn(
			state = listState,
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
			contentPadding = PaddingValues(dimensionResource(R.dimen.screen_padding)),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
		) {
			items(state.bubbles, key = { it.id }) { bubble ->
				ChatBubbleRow(
					bubble = bubble,
					onCreateEntry = onCreateEntry,
				)
			}
		}
	}
}