package app.lade.draft.internal.chiper

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import app.lade.draftdata.DraftModel
import app.lade.entrykind.EntryKind

internal data class BarChipPreset(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val visibleFor: Set<EntryKind>,
    val apply: (DraftModel) -> DraftModel,
    val isActual: (DraftModel) -> Boolean,
)