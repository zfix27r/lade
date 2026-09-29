package app.lade.draft.internal.chat.chip.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.lade.ui.theme.LadeMotion

@Composable
internal fun AnimatedChip(
    key: String,
    content: @Composable () -> Unit,
) {
    key(key) {
        var visible by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(Unit) {
            visible = true
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = LadeMotion.enter()) +
                    expandVertically(animationSpec = LadeMotion.enter()),
            exit = fadeOut(animationSpec = LadeMotion.exit()) +
                    shrinkVertically(animationSpec = LadeMotion.exit()),
        ) {
            content()
        }
    }
}