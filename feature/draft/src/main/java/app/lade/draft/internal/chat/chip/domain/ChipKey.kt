package app.lade.draft.internal.chat.chip.domain

import app.lade.draft.internal.chat.chip.ChipKind

internal data class ChipKey(
    val kind: ChipKind,
    val index: Int,
) {
    fun editTargets(hasFrom: Boolean, hasTo: Boolean): Set<ChipKind> = when (kind) {
        ChipKind.DATE -> buildSet {
            if (hasFrom) add(ChipKind.DATE) // дата-от
            if (hasTo) add(ChipKind.DATE)   // дата-до
        }
        else -> setOf(kind)
    }
}