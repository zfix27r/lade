package app.lade.entrykind.internal

import app.lade.entrykind.EntryKind
import app.lade.entrykind.EntryKindInput
import app.lade.entrykind.EntryKindResolver
import app.lade.entrykind.internal.rule.EntryKindRule
import app.lade.entrykind.internal.rule.EventRule
import app.lade.entrykind.internal.rule.HabitRule
import app.lade.entrykind.internal.rule.RepeatingTaskRule
import app.lade.entrykind.internal.rule.ScheduleRule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EntryKindResolverImpl @Inject constructor(
    scheduleRule: ScheduleRule,
    habitRule: HabitRule,
    repeatingTaskRule: RepeatingTaskRule,
    eventRule: EventRule,
) : EntryKindResolver {

    private val rules: List<EntryKindRule> = listOf(
        scheduleRule,
        habitRule,
        repeatingTaskRule,
        eventRule,
    )

    override fun resolve(input: EntryKindInput): EntryKind {
        for (rule in rules) {
            rule.resolve(input)?.let { return it }
        }
        return EntryKind.TASK
    }
}