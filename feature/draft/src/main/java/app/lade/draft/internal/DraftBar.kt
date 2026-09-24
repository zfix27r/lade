package app.lade.draft.internal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.R
import app.lade.draft.internal.bar.BarView
import app.lade.draft.internal.bar.DraftBarEvent
import app.lade.draft.internal.chat.DraftBarChatViewModel
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DraftBar(
    defaultDate: LocalDate?,
    modifier: Modifier = Modifier,
    viewModel: DraftBarViewModel = hiltViewModel(),
    chatViewModel: DraftBarChatViewModel = hiltViewModel(),
) {
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val hasChanges by viewModel.hasChanges.collectAsStateWithLifecycle()
    val lifecycle by viewModel.lifecycleState.collectAsStateWithLifecycle()
    val barState by viewModel.barState.state.collectAsStateWithLifecycle()

    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(defaultDate) {
        viewModel.setDefaultDate(defaultDate)
    }

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible && barState.isFocused) {
            viewModel.onEvent(DraftBarEvent.Cancel)
        }
    }

    BackHandler(enabled = barState.isFocused && !isImeVisible) {
        viewModel.onEvent(DraftBarEvent.Cancel)
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (barState.isFocused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { viewModel.onEvent(DraftBarEvent.Cancel) },
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
        ) {
            Column {
                if (lifecycle.promptVisible && barState.isFocused) {
                    ResumePrompt(
                        onResume = { viewModel.onEvent(DraftBarEvent.Resume) },
                        onDismiss = { viewModel.onEvent(DraftBarEvent.DismissResume) },
                    )
                }

                BarView(
                    barState = barState,
                    draft = draft,
                    placeholder = stringResource(R.string.draft_placeholder),
                    onKindClick = { viewModel.onEvent(DraftBarEvent.KindClick) },
                    onRawInputChange = { text -> viewModel.barState.updateRawInput(text) },
                    onTextChange = chatViewModel::onTextChange,
                    onFocusChange = viewModel.barState::onFocusChange,
                    onSubmit = chatViewModel::onSubmit,
                    onModeSwitch = { viewModel.onEvent(DraftBarEvent.ModeSwitch) },
                )
            }

            if (hasChanges && !lifecycle.promptVisible && !barState.isFocused) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { viewModel.onEvent(DraftBarEvent.BarTapped) },
                        ),
                )
            }
        }
    }
}