package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entrykind.EntryKind

class EntryKindResolver(
    private val rules: List<EntryKindRule> = DEFAULT_RULES,
) {
    fun resolve(input: EntryKindInput, override: EntryKind? = null): EntryKind {
        if (override != null) return override
        return rules.firstNotNullOfOrNull { it.resolve(input) } ?: EntryKind.TASK
    }

    companion object {
        val DEFAULT_RULES: List<EntryKindRule> = listOf(
            ScheduleRule,
            HabitRule,
            RepeatingTaskRule,
            EventRule,
            DefaultTaskRule,
        )
    }
}