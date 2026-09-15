package app.lade.agenda.data

import app.lade.agenda.api.AgendaModel
import app.lade.agenda.api.AgendaProvider
import app.lade.agenda.api.log.LogActualModel
import app.lade.entrystore.entry.EntryDao
import app.lade.entrystore.log.LogDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgendaProviderImpl @Inject constructor(
    private val entryDao: EntryDao,
    private val goalDao: GoalDao,
    private val logDao: LogDao,
) : AgendaProvider {

    override suspend fun get(entryId: Long, date: LocalDate): AgendaModel? {
        val entry = entryDao.getById(entryId) ?: return null
        val goals = goalDao.getByEntryId(entryId)
        val logs = logDao.getByEntryAndDay(entryId, date.toEpochDay())
        return AgendaModel(
            entry = entry.toApi(),
            goals = goals.map { it.toApi() },
            logs = logs.map { it.toApi() },
        )
    }

    override suspend fun getList(date: LocalDate): List<AgendaModel> {
        val entries = entryDao.observeActive().first()
        val goals = goalDao.getAll()
        val logs = logDao.observeByDay(date.toEpochDay()).first()
        return buildAgendas(entries, goals, logs)
    }

    override fun observeList(date: LocalDate): Flow<List<AgendaModel>> {
        val epochDay = date.toEpochDay()
        return combine(
            entryDao.observeActive(),
            goalDao.observeAll(),
            logDao.observeByDay(epochDay),
        ) { entries, goals, logs ->
            buildAgendas(entries, goals, logs)
        }
    }

    override suspend fun markDay(
        entryId: Long,
        date: LocalDate,
        actuals: Map<Long, LogActualModel>,
    ) {
        val entry = entryDao.getById(entryId)
            ?: error("Entry $entryId not found")
        val goals = goalDao.getByEntryId(entryId)
        val epochDay = date.toEpochDay()
        val now = System.currentTimeMillis()
        val logs = goals.map { goal ->
            val actual = actuals[goal.id]
            LogEntity(
                id = 0,
                entryId = entry.id,
                epochDay = epochDay,
                goalId = goal.id,
                name = goal.title,
                unit = goal.unit,
                plannedAmount = goal.amount,
                plannedRepeat = goal.repeat,
                plannedWeight = goal.weight,
                actualAmount = actual?.amount,
                actualRepeat = actual?.repeat,
                actualWeight = actual?.weight,
                source = null,
                createdAtEpochMs = now,
            )
        }
        logDao.deleteByEntryAndDay(entryId, epochDay)
        logDao.insertAll(logs)
    }

    private fun buildAgendas(
        entries: List<EntryEntity>,
        goals: List<GoalEntity>,
        logs: List<LogEntity>,
    ): List<AgendaModel> {
        val logsByEntry = logs.groupBy { it.entryId }
        val goalsByEntry = goals.groupBy { it.entryId }
        return entries.map { entry ->
            AgendaModel(
                entry = entry.toApi(),
                goals = goalsByEntry[entry.id].orEmpty().map { it.toApi() },
                logs = logsByEntry[entry.id].orEmpty().map { it.toApi() },
            )
        }
    }
}