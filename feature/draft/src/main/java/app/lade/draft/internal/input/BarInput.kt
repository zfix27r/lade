package app.lade.draft.internal.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import app.lade.draft.R
import app.lade.draft.internal.bar.BarState
import app.lade.ui.theme.FieldSizes
import app.lade.ui.theme.LadeMotion
import app.lade.ui.theme.Spacing

@Composable
internal fun BarInput(
    barState: BarState,
    placeholder: String,
    onRawInputChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onFocusChange: (FocusState) -> Unit,
    onSubmit: () -> Unit,
    onModeSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(barState.focusRequestGeneration) {
        if (barState.focusRequestGeneration > 0) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = FieldSizes.minHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        BarInputModeIcon(
            mode = barState.mode,
            onClick = onModeSwitch,
            onSwipe = onModeSwitch,
        )

        BarInputField(
            value = barState.rawInput,
            placeholder = placeholder,
            onFocusChange = onFocusChange,
            onSubmit = onSubmit,
            onValueChange = { text ->
                onRawInputChange(text)
                onTextChange(text)
            },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
        )

        AnimatedVisibility(
            visible = barState.isFocused,
            enter = fadeIn(animationSpec = LadeMotion.enter()),
            exit = fadeOut(animationSpec = LadeMotion.exit()),
        ) {
            BarInputIconButton(
                icon = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.draft_action_send),
                onClick = onSubmit,
                enabled = barState.rawInput.isNotBlank(),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}