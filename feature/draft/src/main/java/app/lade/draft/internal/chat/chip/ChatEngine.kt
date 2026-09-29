package app.lade.draft.internal.chat.chip

import app.lade.chat.api.ChatApi
import app.lade.chat.api.ParserModel
import app.lade.draft.internal.chat.chip.data.ChipKey
import app.lade.draftdata.DraftModel
import javax.inject.Inject

internal class ChipEngine @Inject constructor(
    private val chatApi: ChatApi,
) {
    suspend fun parse(request: ParserModel): ParserModel =
        chatApi.parse(request)

    fun merge(base: DraftModel, result: ParserModel): DraftModel =
        ChipRules.merge(base, result)

    fun remove(model: DraftModel, key: ChipKey): DraftModel =
        ChipRules.remove(model, key)

    fun collectKeys(draft: DraftModel): Set<ChipKey> =
        ChipRules.collectKeys(draft)
}