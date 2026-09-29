package app.lade.draft.internal.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.internal.chat.chip.ChipRow
import app.lade.ui.theme.Spacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BarChatSheet(
    viewModel: ChatViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val entryChips by viewModel.entryChips.collectAsStateWithLifecycle()
    val goalChips by viewModel.goalChips.collectAsStateWithLifecycle()
    val alarmChips by viewModel.alarmChips.collectAsStateWithLifecycle()


    val hasChips = entryChips.isNotEmpty() ||
            goalChips.isNotEmpty() ||
            alarmChips.isNotEmpty()

    if (!hasChips) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            entryChips.forEach { ChipRow(it) }
            goalChips.forEach { ChipRow(it) }
            alarmChips.forEach { ChipRow(it) }
        }
    }
}