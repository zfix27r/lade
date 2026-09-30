package app.lade.draft.internal.chat.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.ui.theme.Spacing

@Composable
internal fun ChipView(
    viewModel: ChipViewModel = hiltViewModel(),
) {
    val entryChips by viewModel.entryChips.collectAsStateWithLifecycle()
    val goalChips by viewModel.goalChips.collectAsStateWithLifecycle()
    val alarmChips by viewModel.alarmChips.collectAsStateWithLifecycle()

    val hasChips = entryChips.isNotEmpty() ||
            goalChips.isNotEmpty() ||
            alarmChips.isNotEmpty()

    if (!hasChips) return

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        entryChips.forEach { ChipRow(it) }
        goalChips.forEach { ChipRow(it) }
        alarmChips.forEach { ChipRow(it) }
    }
}