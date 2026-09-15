package app.lade.agenda.data.log

import app.lade.agenda.api.log.LogError
import app.lade.agenda.api.log.LogSaveGoalModel
import app.lade.agenda.api.log.LogSaveModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogValidator @Inject constructor() {
    fun validate(saveModel: LogSaveModel): LogError? {
        if (saveModel.goals.isEmpty()) return LogError.GoalsEmpty
        saveModel.goals.forEach { goal ->
            validateGoal(goal)?.let { return it }
        }
        return null
    }

    private fun validateGoal(goal: LogSaveGoalModel): LogError? {
        if (goal.goalId <= 0) return LogError.GoalIdMissing
        if (goal.amount != null && goal.amount < 0) return LogError.AmountInvalid
        if (goal.repeat != null && goal.repeat < 0) return LogError.RepeatInvalid
        return null
    }
}