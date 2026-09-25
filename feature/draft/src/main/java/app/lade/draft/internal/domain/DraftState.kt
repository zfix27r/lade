package app.lade.draft.internal.domain

import app.lade.draftdata.DraftModel
import java.time.LocalDate

internal data class DraftState(
    val draft: DraftModel = DraftModel(),
    val original: DraftModel? = null,
    val pending: DraftModel? = null,
    val defaultDate: LocalDate? = null,
)