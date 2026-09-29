package app.lade.draft.internal.ui.bar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import app.lade.draft.api.DraftPhase
import app.lade.draft.internal.chat.BarChatSheet
import app.lade.draft.internal.chiper.BarChiperSheet
import app.lade.draft.internal.editor.BarEditorSheet
import app.lade.draft.internal.input.ui.BarInput
import app.lade.draftdata.DraftModel
import app.lade.ui.theme.LadeMotion
import app.lade.ui.theme.Spacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val TopAppBarHeight = 64.dp

@Composable
internal fun BarView(
    phase: DraftPhase,
    barState: BarState,
    draft: DraftModel,
    placeholder: String,
    onFocusChange: (FocusState) -> Unit,
    onModeSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val view = LocalView.current

    var inputHeight by remember { mutableStateOf(56.dp) }
    var keyboardReady by remember { mutableStateOf(false) }

    val isEditing = phase == DraftPhase.EDIT

    val statusBarHeight = with(density) { WindowInsets.statusBars.getTop(density).toDp() }
    val navBarHeight = with(density) { WindowInsets.navigationBars.getBottom(density).toDp() }
    val imeHeight = with(density) { WindowInsets.ime.getBottom(density).toDp() }
    val screenHeight = with(density) { view.rootView.height.toDp() }

    LaunchedEffect(phase) {
        if (phase == DraftPhase.IDLE) {
            focusManager.clearFocus()
            keyboard?.hide()
        }
    }

    LaunchedEffect(barState.keyboardRequestGeneration) {
        if (barState.keyboardRequestGeneration > 0) keyboard?.show()
    }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            keyboard?.show()
            delay(350.milliseconds)
            keyboardReady = true
        } else {
            keyboardReady = false
            keyboard?.hide()
        }
    }

    val horizontalPadding by animateDpAsState(
        targetValue = if (isEditing) 0.dp else Spacing.lg,
        animationSpec = if (isEditing) LadeMotion.enter() else LadeMotion.exit(),
    )
    val bottomPadding by animateDpAsState(
        targetValue = if (isEditing) 0.dp else Spacing.md,
        animationSpec = if (isEditing) LadeMotion.enter() else LadeMotion.exit(),
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (isEditing) 0.dp else 24.dp,
        animationSpec = if (isEditing) LadeMotion.enter() else LadeMotion.exit(),
    )
    val sheetHeight by animateDpAsState(
        targetValue = if (isEditing && keyboardReady) {
            (screenHeight - statusBarHeight - TopAppBarHeight - navBarHeight - imeHeight - inputHeight)
                .coerceAtLeast(0.dp)
        } else 0.dp,
        animationSpec = if (isEditing) LadeMotion.enter() else LadeMotion.exit(),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = bottomPadding + imeHeight,
            )
            .pointerInput(Unit) { detectTapGestures { } },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = sheetHeight),
            ) {
                when (barState.mode) {
                    BarMode.Chat -> BarChatSheet()
                    BarMode.Chip -> BarChiperSheet(draft = draft)
                    BarMode.Editor -> BarEditorSheet(draft = draft)
                }
            }

            BarInput(
                phase = phase,
                barState = barState,
                placeholder = placeholder,
                onFocusChange = onFocusChange,
                onModeSwitch = onModeSwitch,
                modifier = Modifier
                    .padding(vertical = Spacing.xs)
                    .onSizeChanged { size -> inputHeight = with(density) { size.height.toDp() } },
            )
        }
    }
}