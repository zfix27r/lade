package app.lade.agenda.data.agenda

import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.data.entry.toApi
import app.lade.agenda.data.goal.toApi
import app.lade.agenda.data.log.toApi
import app.lade.agendastore.entry.EntryDao
import app.lade.agendastore.goal.GoalDao
import app.lade.agendastore.log.LogDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgendaComposer @Inject constructor(
    private val entryDao: EntryDao,
    private val goalDao: GoalDao,
    private val logDao: LogDao,
    private val dayProjector: AgendaDayProjector,
) {
    suspend fun get(entryId: Long, date: LocalDate): AgendaModel? {
        val entry = entryDao.getById(entryId)?.toApi() ?: return null
        val goals = goalDao.getByEntryId(entryId).map { it.toApi() }
        val logs = logDao.getByEntryAndDay(entryId, date.toEpochDay()).map { it.toApi() }
        return AgendaModel(
            date = date,
            entry = entry,
            goals = goals,
            logs = logs,
            fromSeries = entry.isSeries,
        )
    }

    fun observeList(date: LocalDate): Flow<List<AgendaModel>> {
        val epochDay = date.toEpochDay()
        return combine(
            entryDao.observeActive(),
            goalDao.observeAll(),
            logDao.observeByDay(epochDay),
        ) { entries, goals, logs ->
            val goalsByEntry = goals.groupBy { it.entryId }
            val logsByEntry = logs.groupBy { it.entryId }
            entries.mapNotNull { entry ->
                val entryModel = entry.toApi()
                if (!dayProjector.appliesTo(entryModel, date)) return@mapNotNull null
                AgendaModel(
                    date = date,
                    entry = entryModel,
                    goals = goalsByEntry[entry.id].orEmpty().map { it.toApi() },
                    logs = logsByEntry[entry.id].orEmpty().map { it.toApi() },
                    fromSeries = entryModel.isSeries,
                )
            }
        }
    }

    fun observeRange(from: LocalDate, to: LocalDate): Flow<List<AgendaModel>> {
        val fromEpoch = from.toEpochDay()
        val toEpoch = to.toEpochDay()
        return combine(
            entryDao.observeActive(),
            goalDao.observeAll(),
            logDao.observeBetween(fromEpoch, toEpoch),
        ) { entries, goals, logs ->
            val goalsByEntry = goals.groupBy { it.entryId }
            val logsByEntry = logs.groupBy { it.entryId }
            val result = mutableListOf<AgendaModel>()
            var date = from
            while (!date.isAfter(to)) {
                val epochDay = date.toEpochDay()
                entries.forEach { entry ->
                    val entryModel = entry.toApi()
                    if (!dayProjector.appliesTo(entryModel, date)) return@forEach
                    val entryLogs = logsByEntry[entry.id].orEmpty()
                        .filter { it.epochDay == epochDay }
                    result += AgendaModel(
                        date = date,
                        entry = entryModel,
                        goals = goalsByEntry[entry.id].orEmpty().map { it.toApi() },
                        logs = entryLogs.map { it.toApi() },
                        fromSeries = entryModel.isSeries,
                    )
                }
                date = date.plusDays(1)
            }
            result
        }
    }
}