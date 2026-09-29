package app.lade.chat.internal

import app.lade.chat.api.ChatApi
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.data.ChatMatchCatalog
import app.lade.chat.internal.pipeline.ChatParseOrchestrator
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatApiImpl @Inject constructor(
    private val orchestrator: ChatParseOrchestrator,
    private val catalog: ChatMatchCatalog,
) : ChatApi {

    override suspend fun parse(model: ParserModel): ParserModel =
        orchestrator.run(
            model = model,
            entries = catalog.activeEntries(),
            today = LocalDate.now(),
        )
}