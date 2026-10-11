package app.lade.parser.internal.rule

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.match.ParserMatchEntry
import app.lade.parser.internal.date.ParserDateL1Rules
import app.lade.parser.internal.date.DateL1WeekendRule
import app.lade.parser.internal.date.ParserDateL2Rules
import app.lade.parser.internal.date.ParserDateL3Rules
import app.lade.parser.internal.duration.ParserDurationL1Rules
import app.lade.parser.internal.goal.ParserGoalsExtractor
import app.lade.parser.internal.kind.ParserKindL2Rules
import app.lade.parser.internal.kind.ParserKindL1Rules
import app.lade.parser.internal.rrule.ParserRruleL1Rules
import app.lade.parser.internal.rrule.ParserRruleL2Rules
import app.lade.parser.internal.time.ParserTimeL1Rules
import app.lade.parser.internal.time.ParserTimeL2Rules
import app.lade.parser.internal.time.ParserTimeL3Rules
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ParserOrchestrator @Inject constructor() {
    private val parserGoalsExtractor = ParserGoalsExtractor()

    fun run(
        model: ParserModel,
        entries: List<ParserMatchEntry> = emptyList(),
        today: LocalDate = LocalDate.now(),
    ): ParserModel {
        val rules = listOf(
            ParserKindL1Rules(),
            ParserDateL1Rules(today),
            DateL1WeekendRule(today),
            ParserTimeL1Rules(),
            ParserDurationL1Rules(),
            ParserRruleL1Rules(),
            ParserKindL2Rules(entries),
            ParserDateL2Rules(today),
            ParserTimeL2Rules(),
            ParserRruleL2Rules(),
            ParserDateL3Rules(today),
            ParserTimeL3Rules(),
        )

        var current = model
        var remaining = model.raw.trim().lowercase()

        for (priority in listOf(ParserPriority.L1, ParserPriority.L2, ParserPriority.L3)) {
            for (rule in rules.filter { it.priority == priority }) {
                val result = rule.apply(current, remaining)
                current = result.model
                remaining = result.remaining
            }
        }

        val extracted = parserGoalsExtractor.extract(remaining)
        if (extracted.goals.isNotEmpty()) {
            current = current.copy(goals = extracted.goals)
        }
        remaining = extracted.remaining

        val entry = current.entry
        if (entry != null && ParserContract.isFind(entry.title)) {
            current = current.copy(
                entry = entry.copy(
                    title = if (remaining.isBlank()) entry.title else ParserContract.found(remaining),
                ),
            )
        }

        return current
    }
}