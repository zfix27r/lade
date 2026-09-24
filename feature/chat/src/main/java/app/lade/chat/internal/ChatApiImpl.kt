package app.lade.chat.internal

import app.lade.chat.api.ChatApi
import app.lade.chat.api.ParseResult
import app.lade.chat.internal.data.ChatMatchCatalog
import app.lade.chat.internal.pipeline.ChatParseOrchestrator
import app.lade.draftdata.DraftModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatApiImpl @Inject constructor(
    private val orchestrator: ChatParseOrchestrator,
    private val catalog: ChatMatchCatalog,
) : ChatApi {
    override suspend fun parse(raw: String, draft: DraftModel): ParseResult =
        orchestrator.run(raw, catalog.activeEntries())
}