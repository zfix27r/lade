package app.lade.calendar.domain

import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.log.LogModel
import app.lade.entrykind.EntryKind
import java.time.LocalDate
import java.time.LocalTime

data class EntryUiModel(
    val entryId: Long,
    val date: LocalDate,
    val kind: EntryKind,
    val title: String,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val fromSeries: Boolean,
    val goals: List<GoalModel>,
    val logs: List<LogModel>,
) {
    val isDone: Boolean get() = logs.any { (it.actualAmount ?: 0) > 0 }
}