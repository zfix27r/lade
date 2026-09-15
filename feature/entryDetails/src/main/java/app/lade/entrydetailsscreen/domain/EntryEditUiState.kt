package app.lade.entrydetailsscreen.domain

import app.lade.agenda.api.entry.EntryError
import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalUnit
import app.lade.agenda.api.overlap.OverlapModel
import app.lade.daypart.domain.DayPartClock
import app.lade.schedule.data.TemporalOptions

data class EntryEditUiState(
    val id: Long = 0,
    val kind: EntryKind = EntryKind.TASK,
    val title: String = "",
    val templateId: Long? = null,
    val goalValueText: String = "1",
    val goalUnit: GoalUnit = GoalUnit.SET,
    val temporal: TemporalOptions = TemporalOptions(),
    val dayPartClock: DayPartClock = DayPartClock.DEFAULT,
    val createdAtEpochMs: Long = 0,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val missingPrompt: EntryError? = null,
    val containment: OverlapModel? = null,
    val pendingCovering: EntryModel? = null,
)