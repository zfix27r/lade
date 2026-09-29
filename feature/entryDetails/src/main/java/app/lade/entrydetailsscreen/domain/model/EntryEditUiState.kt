package app.lade.entrydetailsscreen.domain.model

import app.lade.daypart.domain.DayPartClock
import app.lade.entry.EntryKind
import app.lade.schedule.data.TemporalOptions

data class EntryEditUiState(
    val id: Long = 0,
    val isNew: Boolean = true,
    val isArchived: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val title: String = "",
    val temporal: TemporalOptions = TemporalOptions(),
    val goals: List<GoalDraft> = emptyList(),
    val goalsExplicitlyEnabled: Boolean = false,
    val resolvedEntryKind: EntryKind = EntryKind.TASK,
    val entryKindOverride: EntryKind? = null,
    val createdAtEpochMs: Long = 0,
    val templateId: Long? = null,
    val dayPartClock: DayPartClock = DayPartClock.DEFAULT,
    val isDirty: Boolean = false,
) {
    val effectiveEntryKind: EntryKind get() = entryKindOverride ?: resolvedEntryKind
}