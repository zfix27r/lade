package app.lade.agenda.api.log

import java.time.LocalDate

data class LogSaveModel(
    val date: LocalDate,
    val goals: List<LogSaveGoalModel>,
    val origin: LogOrigin = LogOrigin.UNKNOWN,
)