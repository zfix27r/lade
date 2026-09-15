package app.lade.agenda.data.log

import app.lade.agenda.api.log.LogModel
import app.lade.agenda.api.log.LogOrigin
import app.lade.agenda.api.log.LogSaveModel
import app.lade.agendastore.goal.GoalDao
import app.lade.agendastore.log.LogDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogStore @Inject constructor(
    private val goalDao: GoalDao,
    private val logDao: LogDao,
) {
    suspend fun save(saveModel: LogSaveModel, origin: String) {
        val goalIds = saveModel.goals.map { it.goalId }
        val goals = goalDao.getByIds(goalIds)
        val goalsById = goals.associateBy { it.id }
        val epochDay = saveModel.date.toEpochDay()
        val now = System.currentTimeMillis()

        val logs = saveModel.goals.mapNotNull { mark ->
            val goal = goalsById[mark.goalId] ?: return@mapNotNull null
            LogModel(
                id = 0,
                entryId = goal.entryId,
                epochDay = epochDay,
                goalId = goal.id,
                name = goal.title,
                unit = goal.unit,
                plannedAmount = goal.amount,
                plannedRepeat = goal.repeat,
                plannedWeight = goal.weight,
                actualAmount = mark.amount,
                actualRepeat = mark.repeat,
                actualWeight = mark.weight,
                origin = LogOrigin.fromStorage(origin),
                createdAtEpochMs = now,
            )
        }

        logDao.deleteByGoalIdsAndDay(goalIds, epochDay)
        logDao.insertAll(logs.map { it.toEntity() })
    }
}