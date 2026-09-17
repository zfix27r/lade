package app.lade.calendar.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.resources.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EntryDayMarkActions(
    done: Boolean,
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gap = dimensionResource(R.dimen.spacing_sm)
    val haptics = LocalHapticFeedback.current
    var pulse by remember { mutableStateOf(false) }
    val doneScale by animateFloatAsState(
        targetValue = if (pulse) 1.12f else 1f,
        animationSpec = spring(),
        label = "entryDonePulse",
    )
    LaunchedEffect(pulse) {
        if (pulse) {
            delay(120.milliseconds)
            pulse = false
        }
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        AssistChip(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                pulse = true
                onDone()
            },
            label = { Text(stringResource(R.string.habit_mark_done)) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (done) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
            ),
            modifier = Modifier.scale(doneScale),
        )
        AssistChip(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSkip()
            },
            label = { Text(stringResource(R.string.habit_mark_skip)) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (!done) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
            ),
        )
    }
}