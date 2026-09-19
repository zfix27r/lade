package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entrykind.EntryKind


/** Повтор + (goal или явно включённая секция goal) → HABIT. */
object HabitRule : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? =
        if (input.hasRecurrence && (input.hasGoals || input.goalsExplicitlyEnabled)) {
            EntryKind.HABIT
        } else {
            null
        }
}