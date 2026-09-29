package app.lade.draft.internal.chat.chip

import app.lade.draftdata.DraftModel
import app.lade.humanize.api.Humanize
import kotlinx.coroutines.flow.Flow

internal interface ChipInPort {
    val model: Flow<DraftModel>
    val humanize: Humanize
}