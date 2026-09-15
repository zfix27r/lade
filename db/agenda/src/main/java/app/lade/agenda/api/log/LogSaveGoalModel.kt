package app.lade.agenda.api.log

data class LogSaveGoalModel(
    val goalId: Long,
    val amount: Int? = null,
    val repeat: Int? = null,
    val weight: Double? = null,
)