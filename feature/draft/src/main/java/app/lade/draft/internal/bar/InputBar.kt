package app.lade.draft.internal.bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import app.lade.draft.DraftModel

@Composable
internal fun InputBar(
    state: InputBarState,
    draft: DraftModel,
    placeholder: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onExpand: () -> Unit,
    onConf: () -> Unit,
    onGoal: () -> Unit,
    modifier: Modifier = Modifier,
    config: InputBarConfig = DefaultInputBarConfig,
) {
    val showActions = !draft.isEmpty
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.resetGeneration) {
        if (state.resetGeneration > 0) focusManager.clearFocus()
    }

    LaunchedEffect(state.isFocused) {
        if (!state.isFocused) keyboard?.hide()
    }

    val submit = {
        val value = state.text.trim()
        if (value.isNotEmpty()) {
            onTextChange(value)
            onSubmit()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime)
            .padding(
                horizontal = config.contentHorizontalPadding,
                vertical = config.contentVerticalPadding,
            ),
    ) {
        DraftBarStatus(
            draft = draft,
            onClick = onConf,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(config.fieldCornerRadius))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .animateContentSize(animationSpec = tween(durationMillis = 200)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = config.fieldMinHeight)
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AnimatedVisibility(visible = !draft.isEmpty) {
                    DraftBarKindBadgeIcon(
                        kind = draft.kind,
                        size = config.kindBadgeSize,
                    )
                }

                InputBarField(
                    state = state,
                    placeholder = placeholder,
                    maxLines = config.fieldMaxLines,
                    onSubmit = submit,
                    onTextChange = onTextChange,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                )

                AnimatedVisibility(
                    visible = showActions,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        BarIconButton(
                            icon = Icons.Default.OpenInFull,
                            contentDescription = "Развернуть",
                            onClick = onExpand,
                            buttonSize = config.actionButtonSize,
                            iconSize = config.actionIconSize,
                        )
                        BarIconButton(
                            icon = Icons.Default.Tune,
                            contentDescription = "Настройки",
                            onClick = onConf,
                            buttonSize = config.actionButtonSize,
                            iconSize = config.actionIconSize,
                        )
                        BarIconButton(
                            icon = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Сохранить",
                            onClick = submit,
                            tint = MaterialTheme.colorScheme.primary,
                            buttonSize = config.actionButtonSize,
                            iconSize = config.actionIconSize,
                        )
                        BarIconButton(
                            icon = Icons.Default.Flag,
                            contentDescription = "Цели",
                            onClick = onGoal,
                            buttonSize = config.actionButtonSize,
                            iconSize = config.actionIconSize,
                        )

                    }
                }
            }
        }
    }
}