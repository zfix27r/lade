package app.lade.chat.internal.pipeline

import app.lade.chat.api.ParserContract
import app.lade.chat.api.ParserModel
import app.lade.chat.internal.data.CorpusEntry
import app.lade.chat.internal.pipeline.date.DateL1Rules
import app.lade.chat.internal.pipeline.date.DateL1WeekendRule
import app.lade.chat.internal.pipeline.date.DateL2Rules
import app.lade.chat.internal.pipeline.date.DateL3Rules
import app.lade.chat.internal.pipeline.goal.GoalListExtractor
import app.lade.chat.internal.pipeline.kind.KindCorpusRules
import app.lade.chat.internal.pipeline.kind.KindL1Rules
import app.lade.chat.internal.pipeline.rrule.RruleL1Rules
import app.lade.chat.internal.pipeline.rrule.RruleL2Rules
import app.lade.chat.internal.pipeline.time.TimeL1Rules
import app.lade.chat.internal.pipeline.time.TimeL2Rules
import app.lade.chat.internal.pipeline.time.TimeL3Rules
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatParseOrchestrator @Inject constructor() {
    private val goalListExtractor = GoalListExtractor()

    fun run(
        model: ParserModel,
        entries: List<CorpusEntry> = emptyList(),
        today: LocalDate = LocalDate.now(),
    ): ParserModel {
        val rules = listOf(
            KindL1Rules(),
            DateL1Rules(today),
            DateL1WeekendRule(today),
            TimeL1Rules(),
            RruleL1Rules(),
            KindCorpusRules(entries),
            DateL2Rules(today),
            TimeL2Rules(),
            RruleL2Rules(),
            DateL3Rules(today),
            TimeL3Rules(),
        )

        var current = model
        var remaining = model.raw.trim().lowercase()

        for (priority in listOf(Priority.L1, Priority.L2, Priority.L3)) {
            for (rule in rules.filter { it.priority == priority }) {
                val result = rule.apply(current, remaining)
                current = result.model
                remaining = result.remaining
            }
        }

        val extracted = goalListExtractor.extract(remaining)
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