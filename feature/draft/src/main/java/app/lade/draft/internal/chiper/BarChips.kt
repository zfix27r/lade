package app.lade.draft.internal.chiper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.lade.draftdata.DraftModel
import app.lade.entrykind.EntryKind
import app.lade.ui.theme.LadeMotion
import app.lade.ui.theme.Spacing

@Composable
internal fun BarChips(
    draft: DraftModel,
    isFocused: Boolean,
    onChipApply: ((DraftModel) -> DraftModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val kind = draft.kind?.takeIf { it != EntryKind.UNKNOWN } ?: return
    val inEditing = !draft.isEmpty || isFocused
    if (!inEditing) return

    val actual = barChipPresets.filter { preset ->
        kind in preset.visibleFor && !preset.isActual(draft)
    }

    AnimatedVisibility(
        visible = actual.isNotEmpty(),
        enter = fadeIn(animationSpec = LadeMotion.enter()) + expandVertically(animationSpec = LadeMotion.enter()),
        exit = fadeOut(animationSpec = LadeMotion.exit()) + shrinkVertically(animationSpec = LadeMotion.exit()),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BarChipKindIcon(kind = kind)

            actual.forEach { preset ->
                BarChipItem(
                    preset = preset,
                    onClick = { onChipApply(preset.apply) },
                )
            }
        }
    }
}