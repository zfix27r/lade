package app.lade.draft.internal.editor

import androidx.lifecycle.ViewModel
import app.lade.draft.DraftAlarm
import app.lade.draft.DraftGoal
import app.lade.draft.DraftModel
import app.lade.draft.DraftReminder
import app.lade.draft.internal.DraftApiImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
internal class DraftEditorViewModel @Inject constructor(
    private val api: DraftApiImpl,
) : ViewModel() {

    val draft: StateFlow<DraftModel> = api.draft

    fun onTitleChange(title: String) = api.updateTitle(title)

    fun onDateChange(dateFrom: LocalDate?, dateTo: LocalDate?) =
        api.updateDate(dateFrom, dateTo)

    fun onTimeChange(timeFrom: LocalTime?, timeEnd: LocalTime?) =
        api.updateTime(timeFrom, timeEnd)

    fun onRecurrenceChange(rrule: String?) = api.updateRecurrence(rrule)

    fun onAddGoal() {
        val goals = draft.value.goals + DraftGoal()
        api.updateGoals(goals)
    }

    fun onGoalChange(index: Int, goal: DraftGoal) {
        val goals = draft.value.goals.toMutableList().also { it[index] = goal }
        api.updateGoals(goals)
    }

    fun onRemoveGoal(index: Int) {
        val goals = draft.value.goals.toMutableList().also { it.removeAt(index) }
        api.updateGoals(goals)
    }

    fun onRemindersChange(reminders: List<DraftReminder>) =
        api.updateReminders(reminders)

    fun onAlarmsChange(alarms: List<DraftAlarm>) =
        api.updateAlarms(alarms)

    fun onSave() = api.save()

    fun onCancel() = api.cancel()
}