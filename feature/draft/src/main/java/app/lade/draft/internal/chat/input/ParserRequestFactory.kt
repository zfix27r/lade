package app.lade.draft.internal.chat.input

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserEntryModel
import app.lade.chat.api.ParserModel
import app.lade.draft.internal.chat.chip.ChipKind
import app.lade.draft.internal.chat.chip.data.ChipKey
import app.lade.draft.internal.chat.chip.data.ChipStates
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
        ChipKind.DATE_FROM -> ParserEntryModel(dateFrom = ParserContract.find())
        ChipKind.DATE_TO -> ParserEntryModel(dateTo = ParserContract.find())
        ChipKind.TIME_FROM -> ParserEntryModel(timeFrom = ParserContract.find())
        ChipKind.TIME_END -> ParserEntryModel(timeTo = ParserContract.find())
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
            dateFrom = if (ChipKind.DATE_FROM in committed) ParserContract.skip() else ParserContract.find(),
            dateTo = if (ChipKind.DATE_TO in committed) ParserContract.skip() else ParserContract.find(),
            timeFrom = if (ChipKind.TIME_FROM in committed) ParserContract.skip() else ParserContract.find(),
            timeTo = if (ChipKind.TIME_END in committed) ParserContract.skip() else ParserContract.find(),
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
            if (draft.dateFrom != null && ChipKind.DATE_FROM !in proposed && editing != ChipKind.DATE_FROM) {
                add(ChipKind.DATE_FROM)
            }
            if (draft.dateTo != null && ChipKind.DATE_TO !in proposed && editing != ChipKind.DATE_TO) {
                add(ChipKind.DATE_TO)
            }
            if (draft.timeFrom != null && ChipKind.TIME_FROM !in proposed && editing != ChipKind.TIME_FROM) {
                add(ChipKind.TIME_FROM)
            }
            if (draft.timeEnd != null && ChipKind.TIME_END !in proposed && editing != ChipKind.TIME_END) {
                add(ChipKind.TIME_END)
            }
            if (!draft.rrule.isNullOrBlank() && ChipKind.RRULE !in proposed && editing != ChipKind.RRULE) {
                add(ChipKind.RRULE)
            }
        }
    }
}