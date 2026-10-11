package app.lade.draft.internal.chat.parse

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserEntryModel
import app.lade.parser.api.ParserModel
import app.lade.draft.internal.chat.chip.ChipKind
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draft.internal.chat.chip.domain.ChipStates
import app.lade.draftdata.DraftModel

internal object ParserRequestFactory {

    fun of(
        raw: String,
        editing: ChipKey?,
        states: ChipStates,
        draft: DraftModel,
    ): ParserModel {
        val entry = if (editing != null) {
            entryFor(editing.kind)
        } else {
            entryForAll(states, draft)
        }
        return ParserModel(raw = raw, entry = entry)
    }

    private fun entryFor(kind: ChipKind): ParserEntryModel = when (kind) {
        ChipKind.TITLE -> ParserEntryModel(title = ParserContract.find())
        ChipKind.DATE -> ParserEntryModel(
            dateFrom = ParserContract.find(),
            dateTo = ParserContract.find(),
        )
        ChipKind.TIME -> ParserEntryModel(
            timeFrom = ParserContract.find(),
            timeTo = ParserContract.find(),
        )
        ChipKind.DURATION -> ParserEntryModel(
            durationMinutes = ParserContract.find(),
        )
        ChipKind.RRULE -> ParserEntryModel(rrule = ParserContract.find())
        ChipKind.GOAL -> ParserEntryModel()
        ChipKind.ALARM -> ParserEntryModel()
        ChipKind.REMINDER -> ParserEntryModel()
    }

    private fun entryForAll(states: ChipStates, draft: DraftModel): ParserEntryModel {
        val committed = committedKinds(states, draft)
        return ParserEntryModel(
            kind = ParserContract.find(),
            title = if (ChipKind.TITLE in committed) ParserContract.skip() else ParserContract.find(),
            dateFrom = if (ChipKind.DATE in committed) ParserContract.skip() else ParserContract.find(),
            dateTo = if (ChipKind.DATE in committed) ParserContract.skip() else ParserContract.find(),
            timeFrom = if (ChipKind.TIME in committed) ParserContract.skip() else ParserContract.find(),
            timeTo = if (ChipKind.TIME in committed) ParserContract.skip() else ParserContract.find(),
            durationMinutes = if (ChipKind.DURATION in committed) ParserContract.skip() else ParserContract.find(),
            rrule = if (ChipKind.RRULE in committed) ParserContract.skip() else ParserContract.find(),
        )
    }

    private fun committedKinds(states: ChipStates, draft: DraftModel): Set<ChipKind> {
        val editing = states.editingKey?.kind
        val proposed = states.proposedKeys.map { it.kind }.toSet()
        return buildSet {
            if (draft.title.isNotBlank() && ChipKind.TITLE !in proposed && editing != ChipKind.TITLE) {
                add(ChipKind.TITLE)
            }
            if (hasDate(draft) && ChipKind.DATE !in proposed && editing != ChipKind.DATE) {
                add(ChipKind.DATE)
            }
            if (hasTime(draft) && ChipKind.TIME !in proposed && editing != ChipKind.TIME) {
                add(ChipKind.TIME)
            }
            if (hasDuration(draft) && ChipKind.DURATION !in proposed && editing != ChipKind.DURATION) {
                add(ChipKind.DURATION)
            }
            if (!draft.rrule.isNullOrBlank() && ChipKind.RRULE !in proposed && editing != ChipKind.RRULE) {
                add(ChipKind.RRULE)
            }
        }
    }

    private fun hasDate(draft: DraftModel): Boolean =
        draft.dateFrom != null || draft.dateTo != null

    private fun hasTime(draft: DraftModel): Boolean =
        draft.timeFrom != null || draft.timeEnd != null

    private fun hasDuration(draft: DraftModel): Boolean =
        draft.durationMinutes != null
}