package app.lade.draft.internal.input.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.R
import app.lade.draft.internal.di.BarInputEntryPoint
import app.lade.draft.internal.input.BarInputSubmit
import app.lade.draft.internal.ui.bar.BarState
import app.lade.ui.theme.FieldSizes
import app.lade.ui.theme.LadeMotion
import app.lade.ui.theme.Spacing
import dagger.hilt.android.EntryPointAccessors

@Composable
internal fun BarInput(
    barState: BarState,
    placeholder: String,
    onFocusChange: (FocusState) -> Unit,
    onModeSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val port = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            BarInputEntryPoint::class.java,
        ).barInputControlPort()
    }

    val prefill by port.prefill.collectAsStateWithLifecycle()
    val submit by port.submit.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    var fieldValue by remember {
        mutableStateOf(TextFieldValue("", TextRange(0)))
    }

    LaunchedEffect(Unit) {
        port.clearInput.collect {
            fieldValue = TextFieldValue("", TextRange(0))
        }
    }

    LaunchedEffect(prefill) {
        val incoming = prefill ?: return@LaunchedEffect
        fieldValue = TextFieldValue(
            text = incoming,
            selection = TextRange(incoming.length),
        )
    }

    LaunchedEffect(barState.focusRequestGeneration) {
        if (barState.focusRequestGeneration > 0) {
            focusRequester.requestFocus()
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
            fieldValue = fieldValue,
            onFieldValueChange = { newValue ->
                fieldValue = newValue
                port.onChangeText(newValue.text)
            },
            placeholder = placeholder,
            onFocusChange = onFocusChange,
            onSubmit = { port.onClickSubmit() },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
        )

        val enterSpec = LadeMotion.enter<Float>()
        val exitSpec = LadeMotion.exit<Float>()

        AnimatedVisibility(
            visible = submit != BarInputSubmit.None,
            enter = fadeIn(animationSpec = enterSpec),
            exit = fadeOut(animationSpec = exitSpec),
        ) {
            when (submit) {
                BarInputSubmit.None -> Unit
                BarInputSubmit.Send -> BarInputIconButton(
                    icon = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.draft_action_send),
                    onClick = port::onClickSubmit,
                    tint = MaterialTheme.colorScheme.primary,
                )

                BarInputSubmit.Commit -> BarInputIconButton(
                    icon = Icons.Outlined.Done,
                    contentDescription = stringResource(R.string.draft_action_commit),
                    onClick = port::onClickSubmit,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}