package app.lade.chat.internal.pipeline

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.ParseResult
import app.lade.chat.api.ParsedField
import app.lade.chat.internal.data.CorpusEntry
import app.lade.chat.internal.pipeline.date.DateL1Rules
import app.lade.chat.internal.pipeline.date.DateL1WeekendRule
import app.lade.chat.internal.pipeline.date.DateL2Rules
import app.lade.chat.internal.pipeline.date.DateL3Rules
import app.lade.chat.internal.pipeline.date.DateL4Rules
import app.lade.chat.internal.pipeline.goal.GoalListExtractor
import app.lade.chat.internal.pipeline.kind.KindCorpusRules
import app.lade.chat.internal.pipeline.kind.KindL1Rules
import app.lade.chat.internal.pipeline.rrule.RruleL1Rules
import app.lade.chat.internal.pipeline.rrule.RruleL2Rules
import app.lade.chat.internal.pipeline.time.TimeL1Rules
import app.lade.chat.internal.pipeline.time.TimeL2Rules
import app.lade.chat.internal.pipeline.time.TimeL3Rules
import app.lade.chat.internal.state.ParsePipeline
import app.lade.chat.internal.state.ParseState
import app.lade.entrykind.EntryKind
import app.lade.entrykind.EntryKindInput
import app.lade.entrykind.EntryKindResolver
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatParseOrchestrator @Inject constructor(
    private val entryKindResolver: EntryKindResolver,
) {
    private val goalListExtractor = GoalListExtractor()

    fun run(
        raw: String,
        entries: List<CorpusEntry> = emptyList(),
        today: LocalDate = LocalDate.now(),
    ): ParseResult {
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
            DateL4Rules(today),
        )

        val result = ParsePipeline(rules).run(raw.trim().lowercase(), ParseState())

        val extracted = goalListExtractor.extract(result.remaining)

        val withGoals = result.copy(
            goals = extracted.goals,
            remaining = extracted.remaining,
        )

        if (withGoals.fields.none { it.key == FieldKey.KIND }) {
            val kind = resolveKind(withGoals)
            return withGoals.copy(
                fields = withGoals.fields + ParsedField(
                    key = FieldKey.KIND,
                    value = FieldValue.Kind(kind, null),
                    match = "",
                ),
            )
        }
        return withGoals
    }

    private fun resolveKind(result: ParseResult): EntryKind {
        fun dateOf(key: FieldKey) =
            (result.fields.firstOrNull { it.key == key }?.value as? FieldValue.Date)?.value

        fun timeOf(key: FieldKey) =
            (result.fields.firstOrNull { it.key == key }?.value as? FieldValue.Time)?.value

        val rrule = (result.fields.firstOrNull { it.key == FieldKey.RRULE }?.value as? FieldValue.Text)?.value
        val hasGoals = result.goals.isNotEmpty()

        return entryKindResolver.resolve(
            EntryKindInput(
                dateFrom = dateOf(FieldKey.DATE_FROM),
                dateTo = dateOf(FieldKey.DATE_TO),
                timeFrom = timeOf(FieldKey.TIME_FROM),
                timeEnd = timeOf(FieldKey.TIME_END),
                rrule = rrule,
                hasGoals = hasGoals,
            ),
        )
    }
}