package app.lade.draft.internal.bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import app.lade.draft.internal.chat.BarChatSheet
import app.lade.draft.internal.chiper.BarChiperSheet
import app.lade.draft.internal.editor.BarEditorSheet
import app.lade.draft.internal.input.BarInput
import app.lade.draftdata.DraftModel
import app.lade.entrykind.EntryKind
import app.lade.ui.theme.LadeMotion
import app.lade.ui.theme.Spacing

@Composable
internal fun BarView(
    barState: BarState,
    draft: DraftModel,
    placeholder: String,
    onKindClick: () -> Unit,
    onRawInputChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onFocusChange: (FocusState) -> Unit,
    onSubmit: () -> Unit,
    onModeSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(barState.resetGeneration) {
        if (barState.resetGeneration > 0) focusManager.clearFocus()
    }

    LaunchedEffect(barState.isFocused) {
        if (!barState.isFocused) keyboard?.hide()
    }

    val sheetState = remember { MutableTransitionState(barState.isFocused) }
    var barExpanded by remember { mutableStateOf(barState.isFocused) }

    LaunchedEffect(barState.isFocused) {
        barExpanded = barState.isFocused
        sheetState.targetState = barState.isFocused
    }

    val horizontalPadding by animateDpAsState(
        targetValue = if (barExpanded) 0.dp else Spacing.lg,
        animationSpec = if (barExpanded) LadeMotion.enter() else LadeMotion.exit(),
    )
    val bottomPadding by animateDpAsState(
        targetValue = if (barExpanded) 0.dp else Spacing.md,
        animationSpec = if (barExpanded) LadeMotion.enter() else LadeMotion.exit(),
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (barExpanded) 0.dp else 24.dp,
        animationSpec = if (barExpanded) LadeMotion.enter() else LadeMotion.exit(),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = bottomPadding,
            )
            .pointerInput(Unit) {
                detectTapGestures { }
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .animateContentSize(animationSpec = LadeMotion.exit())
        ) {
            AnimatedVisibility(
                visibleState = sheetState,
                enter = fadeIn(animationSpec = LadeMotion.enter()),
                exit = fadeOut(animationSpec = LadeMotion.exit()),
            ) {
                when (barState.mode) {
                    BarMode.Chat -> BarChatSheet(
                        kind = draft.kind ?: EntryKind.TASK,
                        title = draft.title,
                        onKindClick = onKindClick,
                    )
                    BarMode.Chip -> BarChiperSheet(draft = draft)
                    BarMode.Editor -> BarEditorSheet(draft = draft)
                }
            }

            BarInput(
                barState = barState,
                placeholder = placeholder,
                onRawInputChange = onRawInputChange,
                onTextChange = onTextChange,
                onFocusChange = onFocusChange,
                onSubmit = onSubmit,
                onModeSwitch = onModeSwitch,
                modifier = Modifier.padding(vertical = Spacing.xs),
            )
        }
    }
}