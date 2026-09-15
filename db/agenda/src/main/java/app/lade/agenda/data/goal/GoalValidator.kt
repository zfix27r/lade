package app.lade.agenda.data.goal

import app.lade.agenda.api.goal.GoalError
import app.lade.agenda.api.goal.GoalModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalValidator @Inject constructor() {
    fun validate(goals: List<GoalModel>): GoalError? {
        goals.forEach { goal ->
            validateOne(goal)?.let { return it }
        }
        return null
    }

    private fun validateOne(goal: GoalModel): GoalError? {
        if (goal.title.isBlank()) return GoalError.TitleMissing
        if (goal.unit.isBlank()) return GoalError.UnitMissing
        if (goal.amount != null && goal.amount < 0) return GoalError.AmountInvalid
        if (goal.repeat != null && goal.repeat < 0) return GoalError.RepeatInvalid
        return null
    }
}