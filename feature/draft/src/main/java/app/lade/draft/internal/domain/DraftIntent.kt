package app.lade.draft.internal.domain

import app.lade.draftdata.DraftAlarm
import app.lade.draftdata.DraftGoal
import app.lade.draftdata.DraftModel
import app.lade.draftdata.DraftReminder
import java.time.LocalDate
import java.time.LocalTime

internal sealed interface DraftIntent {
    data class SetDefaultDate(val date: LocalDate?) : DraftIntent
    data class Open(val entryId: Long?) : DraftIntent
    data object Reset : DraftIntent
    data object StashAndReset : DraftIntent
    data object RestorePending : DraftIntent
    data object ClearPending : DraftIntent

    data class UpdateTitle(val title: String) : DraftIntent
    data class UpdateDate(val dateFrom: LocalDate?, val dateTo: LocalDate?) : DraftIntent
    data class UpdateTime(val timeFrom: LocalTime?, val timeEnd: LocalTime?) : DraftIntent
    data class UpdateRecurrence(val rrule: String?) : DraftIntent
    data class UpdateGoals(val goals: List<DraftGoal>) : DraftIntent
    data class UpdateAlarms(val alarms: List<DraftAlarm>) : DraftIntent
    data class UpdateReminders(val reminders: List<DraftReminder>) : DraftIntent
    data class Update(val transform: (DraftModel) -> DraftModel) : DraftIntent

    data object Save : DraftIntent
    data object Cancel : DraftIntent
}