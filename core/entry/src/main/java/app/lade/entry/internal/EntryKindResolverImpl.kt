package app.lade.entry.internal

import app.lade.entry.EntryKind
import app.lade.entry.EntryModel
import app.lade.entry.EntryKindResolver
import app.lade.entry.internal.rule.EntryKindRule
import app.lade.entry.internal.rule.EventRule
import app.lade.entry.internal.rule.HabitRule
import app.lade.entry.internal.rule.RepeatingTaskRule
import app.lade.entry.internal.rule.ScheduleRule
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

    override fun resolve(input: EntryModel): EntryKind {
        for (rule in rules) {
            rule.resolve(input)?.let { return it }
        }
        return EntryKind.TASK
    }
}