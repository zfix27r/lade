package app.lade.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.R
import app.lade.entry.domain.models.EntryKind

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatInputBar(
	bottomNavHeight: Dp,
	input: String,
	sending: Boolean,
	choices: List<ChatChoiceOption>,
	onInputChange: (String) -> Unit,
	onSend: () -> Unit,
	onChoice: (ChatChoiceOption) -> Unit,
) {
	val bottomInsets = if (bottomNavHeight > 0.dp) {
		WindowInsets.ime.union(WindowInsets(bottom = bottomNavHeight))
	} else {
		WindowInsets.ime
	}
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.windowInsetsPadding(bottomInsets.only(WindowInsetsSides.Bottom)),
	) {
		if (choices.isNotEmpty()) {
			Text(
				text = stringResource(R.string.chat_choice_prompt),
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
			)
			FlowRow(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
			) {
				choices.forEach { option ->
					FilterChip(
						selected = false,
						onClick = { onChoice(option) },
						label = { Text(option.label) },
					)
				}
			}
		}
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(dimensionResource(R.dimen.screen_padding)),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
		) {
			OutlinedTextField(
				value = input,
				onValueChange = onInputChange,
				modifier = Modifier.weight(1f),
				placeholder = { Text(stringResource(R.string.chat_input_hint)) },
				singleLine = true,
				enabled = !sending,
			)
			IconButton(
				onClick = onSend,
				enabled = !sending && input.isNotBlank(),
			) {
				Icon(
					Icons.AutoMirrored.Filled.Send,
					contentDescription = stringResource(R.string.chat_send),
				)
			}
		}
	}
}

@Composable
private fun ChatBubbleRow(
	bubble: ChatBubble,
	onCreateEntry: (kind: String, title: String) -> Unit,
) {
	val align = if (bubble.kind == ChatBubbleKind.USER) Alignment.CenterEnd else Alignment.CenterStart
	val bg = when (bubble.kind) {
		ChatBubbleKind.USER -> MaterialTheme.colorScheme.primaryContainer
		ChatBubbleKind.OK -> MaterialTheme.colorScheme.secondaryContainer
		ChatBubbleKind.ERROR -> MaterialTheme.colorScheme.errorContainer
		ChatBubbleKind.HINT -> MaterialTheme.colorScheme.surfaceVariant
	}
	val fg = when (bubble.kind) {
		ChatBubbleKind.USER -> MaterialTheme.colorScheme.onPrimaryContainer
		ChatBubbleKind.OK -> MaterialTheme.colorScheme.onSecondaryContainer
		ChatBubbleKind.ERROR -> MaterialTheme.colorScheme.onErrorContainer
		ChatBubbleKind.HINT -> MaterialTheme.colorScheme.onSurfaceVariant
	}
	Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
		Column(
			modifier = Modifier
				.fillMaxWidth(0.88f)
				.background(bg, RoundedCornerShape(dimensionResource(R.dimen.spacing_md)))
				.padding(dimensionResource(R.dimen.spacing_md)),
		) {
			Text(
				text = bubbleText(bubble),
				style = MaterialTheme.typography.bodyLarge,
				color = fg,
			)
			bubble.detail?.takeIf { it.isNotBlank() }?.let { detail ->
				Text(
					text = detail,
					style = MaterialTheme.typography.bodyMedium,
					color = fg,
					modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_xs)),
				)
			}
			val createTitle = bubble.createTitle
			if (bubble.messageKey == "habit_missing" && !createTitle.isNullOrBlank()) {
				val kind = bubble.createKind ?: EntryKind.HABIT.storage
				TextButton(onClick = { onCreateEntry(kind, createTitle) }) {
					Text(stringResource(R.string.chat_create_habit, createTitle))
				}
			}
		}
	}
}

@Composable
private fun bubbleText(bubble: ChatBubble): String {
	if (bubble.text.isNotBlank()) return bubble.text
	return when (bubble.messageKey) {
		"hint" -> stringResource(R.string.chat_hint_examples)
		"empty" -> stringResource(R.string.chat_err_empty)
		"no_template" -> stringResource(R.string.chat_err_no_template)
		"bad_duration" -> stringResource(R.string.chat_err_bad_duration)
		"habit_not_found" -> stringResource(R.string.chat_err_habit_not_found)
		"habit_missing" -> stringResource(R.string.chat_err_habit_missing)
		"choice_which_entry" -> stringResource(R.string.chat_choice_prompt)
		"no_category" -> stringResource(R.string.chat_err_no_category)
		"block_overlap" -> stringResource(R.string.chat_err_block_overlap)
		"habit" -> stringResource(R.string.chat_ok_habit)
		"block" -> stringResource(R.string.chat_ok_block)
		"task" -> stringResource(R.string.chat_ok_task)
		else -> stringResource(R.string.chat_err_parse)
	}
}
