package app.lade.agenda.api.log

sealed class LogError {
    data object DateMissing : LogError()
    data object GoalsEmpty : LogError()
    data object GoalIdMissing : LogError()
    data object AmountInvalid : LogError()
    data object RepeatInvalid : LogError()
}