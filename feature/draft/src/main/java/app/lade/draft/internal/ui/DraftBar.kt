package app.lade.draft.internal.ui

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
import app.lade.draft.api.DraftPhase
import app.lade.draft.internal.chat.DraftBarChatViewModel
import app.lade.draft.internal.input.ResumePrompt
import app.lade.draft.internal.ui.bar.BarView
import app.lade.draft.internal.ui.bar.DraftBarEvent
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DraftBar(
    defaultDate: LocalDate?,
    modifier: Modifier = Modifier,
    viewModel: DraftBarViewModel = hiltViewModel(),
    chatViewModel: DraftBarChatViewModel = hiltViewModel(),
) {
    val bar by viewModel.bar.collectAsStateWithLifecycle()
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val lifecycle by viewModel.lifecycleState.collectAsStateWithLifecycle()

    val isImeVisible = WindowInsets.isImeVisible
    val isEditing = lifecycle.phase == DraftPhase.EDIT

    LaunchedEffect(defaultDate) {
        viewModel.setDefaultDate(defaultDate)
    }

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible && isEditing) {
            viewModel.onEvent(DraftBarEvent.Cancel)
        }
    }

    BackHandler(enabled = isEditing && !isImeVisible) {
        viewModel.onEvent(DraftBarEvent.Cancel)
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isEditing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                if (lifecycle.promptVisible) {
                    ResumePrompt(
                        onResume = { viewModel.onEvent(DraftBarEvent.Resume) },
                        onDismiss = { viewModel.onEvent(DraftBarEvent.DismissResume) },
                    )
                }

                BarView(
                    phase = lifecycle.phase,
                    barState = bar,
                    draft = draft,
                    placeholder = stringResource(R.string.draft_placeholder),
                    onKindClick = { viewModel.onEvent(DraftBarEvent.KindClick) },
                    onRawInputChange = viewModel::onRawInputChange,
                    onTextChange = chatViewModel::onTextChange,
                    onFocusChange = viewModel::onBarFocusChange,
                    onSubmit = chatViewModel::onSubmit,
                    onModeSwitch = { viewModel.onEvent(DraftBarEvent.ModeSwitch) },
                )
            }
        }
    }
}