package app.lade.draft.internal.chat.parse

import app.lade.parser.api.ParserApi
import app.lade.parser.api.ParserModel
import app.lade.draft.internal.chat.chip.ChipRules
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draftdata.DraftModel
import javax.inject.Inject

internal class ParseEngine @Inject constructor(
    private val parserApi: ParserApi,
) {
    suspend fun parse(request: ParserModel): ParserModel =
        parserApi.parse(request)

    fun merge(base: DraftModel, result: ParserModel): DraftModel =
        ChipRules.merge(base, result)

    fun remove(model: DraftModel, key: ChipKey): DraftModel =
        ChipRules.remove(model, key)

    fun collectKeys(draft: DraftModel): Set<ChipKey> =
        ChipRules.collectKeys(draft)
}