package app.lade.agenda.data.goal

import app.lade.agenda.api.goal.GoalModel
import app.lade.agendastore.goal.GoalDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalStore @Inject constructor(
    private val goalDao: GoalDao,
) {
    suspend fun save(entryId: Long, goals: List<GoalModel>) {
        goalDao.deleteByEntryId(entryId)
        goalDao.insertAll(goals.map { it.toEntity(entryId) })
    }
}