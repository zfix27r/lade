package app.lade.agenda.api.goal

sealed class GoalError {
    data object TitleMissing : GoalError()
    data object UnitMissing : GoalError()
    data object AmountInvalid : GoalError()
    data object RepeatInvalid : GoalError()
}