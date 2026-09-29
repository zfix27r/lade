package app.lade.draft.internal.input.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.api.DraftPhase
import app.lade.draft.internal.di.BarInputEntryPoint
import app.lade.draft.internal.ui.bar.BarState
import app.lade.ui.theme.FieldSizes
import app.lade.ui.theme.LadeMotion
import app.lade.ui.theme.Spacing
import dagger.hilt.android.EntryPointAccessors

@Composable
internal fun BarInput(
    phase: DraftPhase,
    barState: BarState,
    placeholder: String,
    onFocusChange: (FocusState) -> Unit,
    onModeSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val holder = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            BarInputEntryPoint::class.java,
        ).barInputHolder()
    }

    val prefill by holder.prefill.collectAsStateWithLifecycle()
    val action by holder.action.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    var fieldValue by remember {
        mutableStateOf(TextFieldValue("", TextRange(0)))
    }

    LaunchedEffect(phase) {
        if (phase == DraftPhase.IDLE) {
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
                holder.onValueChange(newValue.text)
            },
            placeholder = placeholder,
            onFocusChange = onFocusChange,
            onSubmit = { },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
        )

        val enterSpec = LadeMotion.enter<Float>()
        val exitSpec = LadeMotion.exit<Float>()

        AnimatedVisibility(
            visible = action.action != null,
            enter = fadeIn(animationSpec = enterSpec),
            exit = fadeOut(animationSpec = exitSpec),
        ) {
            AnimatedContent(
                targetState = action.action,
                transitionSpec = {
                    (fadeIn(animationSpec = enterSpec) + scaleIn(
                        animationSpec = enterSpec,
                        initialScale = 0.8f,
                    )).togetherWith(
                        fadeOut(animationSpec = exitSpec) + scaleOut(
                            animationSpec = exitSpec,
                            targetScale = 0.8f,
                        )
                    )
                },
                label = "bar-input-action",
            ) { target ->
                if (target != null) {
                    BarInputIconButton(
                        icon = target.icon(),
                        contentDescription = target.description(),
                        onClick = holder::onActionClick,
                        enabled = action.actionEnabled,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}