package app.lade.parser.internal

import app.lade.parser.api.ParserApi
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.match.ParserMatchCatalog
import app.lade.parser.internal.rule.ParserOrchestrator
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ParserApiImpl @Inject constructor(
    private val orchestrator: ParserOrchestrator,
    private val catalog: ParserMatchCatalog,
) : ParserApi {

    override suspend fun parse(model: ParserModel): ParserModel =
        orchestrator.run(
            model = model,
            entries = catalog.activeEntries(),
            today = LocalDate.now(),
        )
}