package app.lade.calendardata.internal

import app.lade.agendastore.entry.EntryDao
import app.lade.agendastore.entry.EntryEntity
import app.lade.agendastore.goal.GoalDao
import app.lade.agendastore.goal.GoalEntity
import app.lade.agendastore.log.LogDao
import app.lade.agendastore.log.LogEntity
import app.lade.calendardata.api.CalendarCardModel
import app.lade.calendardata.api.CalendarDataApi
import app.lade.calendardata.api.CalendarGoalModel
import app.lade.calendardata.api.DayProgress
import app.lade.calendardata.internal.mapper.isAlarm
import app.lade.calendardata.internal.mapper.isDone
import app.lade.calendardata.internal.mapper.isSeries
import app.lade.calendardata.internal.mapper.toEntryKind
import app.lade.calendardata.internal.mapper.toLocalTimeFrom
import app.lade.calendardata.internal.mapper.toLocalTimeTo
import app.lade.goal.GoalUnit
import app.lade.humanize.api.Humanize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CalendarDataApiImpl @Inject constructor(
    private val entryDao: EntryDao,
    private val goalDao: GoalDao,
    private val logDao: LogDao,
    private val projector: CalendarDayProjector,
    private val humanize: Humanize,
) : CalendarDataApi {

    override fun observeList(date: LocalDate): Flow<List<CalendarCardModel>> {
        val epochDay = date.toEpochDay()
        return combine(
            entryDao.observeActive(),
            goalDao.observeAll(),
            logDao.observeByDay(epochDay),
        ) { entries, goals, logs ->
            buildCards(date, entries, goals, logs)
        }
    }

    override fun observeRange(from: LocalDate, to: LocalDate): Flow<List<CalendarCardModel>> {
        val fromEpoch = from.toEpochDay()
        val toEpoch = to.toEpochDay()
        return combine(
            entryDao.observeActive(),
            goalDao.observeAll(),
            logDao.observeBetween(fromEpoch, toEpoch),
        ) { entries, goals, logs ->
            val result = mutableListOf<CalendarCardModel>()
            var date = from
            while (!date.isAfter(to)) {
                result += buildCards(date, entries, goals, logs)
                date = date.plusDays(1)
            }
            result
        }
    }

    override fun observeMarkedDates(
        from: LocalDate,
        to: LocalDate,
    ): Flow<Map<LocalDate, DayProgress>> {
        val fromEpoch = from.toEpochDay()
        val toEpoch = to.toEpochDay()
        return combine(
            entryDao.observeActive(),
            goalDao.observeAll(),
            logDao.observeBetween(fromEpoch, toEpoch),
        ) { entries, goals, logs ->
            val goalsByEntry = goals.groupBy { it.entryId }
            val logsByEntry = logs.groupBy { it.entryId }
            val result = mutableMapOf<LocalDate, DayProgress>()
            var date = from
            while (!date.isAfter(to)) {
                val epochDay = date.toEpochDay()
                var total = 0
                var done = 0
                var hasEntry = false
                entries.forEach { entry ->
                    if (!projector.appliesTo(entry, date)) return@forEach
                    hasEntry = true
                    val entryGoals = goalsByEntry[entry.id].orEmpty()
                    if (entryGoals.isEmpty()) return@forEach
                    val entryLogs = logsByEntry[entry.id].orEmpty()
                        .filter { it.epochDay == epochDay }
                    val logsByGoal = entryLogs.filter { it.goalId != null }
                        .associateBy { it.goalId }
                    entryGoals.forEach { goal ->
                        total++
                        if (goal.isDone(logsByGoal[goal.id])) done++
                    }
                }
                if (hasEntry) {
                    result[date] = DayProgress(total = total, done = done)
                }
                date = date.plusDays(1)
            }
            result
        }
    }

    override suspend fun get(entryId: Long, date: LocalDate): CalendarCardModel? {
        val entry = entryDao.getById(entryId) ?: return null
        if (!projector.appliesTo(entry, date)) return null
        val goals = goalDao.getByEntryId(entryId)
        val logs = logDao.getByEntryAndDay(entryId, date.toEpochDay())
        return buildCard(date, entry, goals, logs)
    }

    override suspend fun toggleGoal(entryId: Long, date: LocalDate, goalId: Long) {
        val goals = goalDao.getByEntryId(entryId)
        val goal = goals.firstOrNull { it.id == goalId } ?: return
        val existing = logDao.getByGoalAndDay(goalId, date.toEpochDay())
        val alreadyDone = existing.any { (it.actualAmount ?: 0) > 0 }
        val log = LogEntity(
            entryId = entryId,
            epochDay = date.toEpochDay(),
            goalId = goalId,
            name = goal.title,
            unit = goal.unit,
            plannedAmount = goal.amount,
            plannedRepeat = goal.repeat,
            plannedWeight = goal.weight,
            actualAmount = if (alreadyDone) 0 else (goal.amount ?: 1),
            actualRepeat = if (alreadyDone) 0 else (goal.repeat ?: 0),
            actualWeight = goal.weight,
            origin = "calendar",
            createdAtEpochMs = System.currentTimeMillis(),
        )
        logDao.deleteByGoalAndDay(goalId, date.toEpochDay())
        logDao.insertAll(listOf(log))
    }

    override suspend fun toggleAllGoals(entryId: Long, date: LocalDate) {
        val goals = goalDao.getByEntryId(entryId)
        if (goals.isEmpty()) return
        val existing = logDao.getByEntryAndDay(entryId, date.toEpochDay())
        val logsByGoal = existing.filter { it.goalId != null }.associateBy { it.goalId }
        val allDone = goals.all { goal ->
            val log = logsByGoal[goal.id]
            goal.isDone(log)
        }
        val logs = goals.map { goal ->
            LogEntity(
                entryId = entryId,
                epochDay = date.toEpochDay(),
                goalId = goal.id,
                name = goal.title,
                unit = goal.unit,
                plannedAmount = goal.amount,
                plannedRepeat = goal.repeat,
                plannedWeight = goal.weight,
                actualAmount = if (allDone) 0 else (goal.amount ?: 1),
                actualRepeat = if (allDone) 0 else (goal.repeat ?: 0),
                actualWeight = goal.weight,
                origin = "calendar",
                createdAtEpochMs = System.currentTimeMillis(),
            )
        }
        logDao.deleteByGoalIdsAndDay(goals.map { it.id }, date.toEpochDay())
        logDao.insertAll(logs)
    }

    override suspend fun skipAllGoals(entryId: Long, date: LocalDate) {
        val goals = goalDao.getByEntryId(entryId)
        if (goals.isEmpty()) return
        val logs = goals.map { goal ->
            LogEntity(
                entryId = entryId,
                epochDay = date.toEpochDay(),
                goalId = goal.id,
                name = goal.title,
                unit = goal.unit,
                plannedAmount = goal.amount,
                plannedRepeat = goal.repeat,
                plannedWeight = goal.weight,
                actualAmount = 0,
                actualRepeat = 0,
                actualWeight = goal.weight,
                origin = "calendar",
                createdAtEpochMs = System.currentTimeMillis(),
            )
        }
        logDao.deleteByGoalIdsAndDay(goals.map { it.id }, date.toEpochDay())
        logDao.insertAll(logs)
    }

    override suspend fun markEventVisited(entryId: Long, date: LocalDate, visited: Boolean) {
        val log = LogEntity(
            entryId = entryId,
            epochDay = date.toEpochDay(),
            goalId = null,
            actualAmount = if (visited) 1 else 0,
            origin = "calendar",
            createdAtEpochMs = System.currentTimeMillis(),
        )
        logDao.deleteByEntryAndDay(entryId, date.toEpochDay())
        logDao.insertAll(listOf(log))
    }

    private fun buildCards(
        date: LocalDate,
        entries: List<EntryEntity>,
        goals: List<GoalEntity>,
        logs: List<LogEntity>,
    ): List<CalendarCardModel> {
        val epochDay = date.toEpochDay()
        val goalsByEntry = goals.groupBy { it.entryId }
        val logsByEntry = logs.filter { it.epochDay == epochDay }.groupBy { it.entryId }
        return entries.mapNotNull { entry ->
            if (!projector.appliesTo(entry, date)) return@mapNotNull null
            buildCard(
                date = date,
                entry = entry,
                goals = goalsByEntry[entry.id].orEmpty(),
                logs = logsByEntry[entry.id].orEmpty(),
            )
        }
    }

    private fun buildCard(
        date: LocalDate,
        entry: EntryEntity,
        goals: List<GoalEntity>,
        logs: List<LogEntity>,
    ): CalendarCardModel? {
        val kind = entry.toEntryKind() ?: return null
        val logsByGoal = logs.filter { it.goalId != null }.associateBy { it.goalId }
        val isVisited = logs.any { it.goalId == null && (it.actualAmount ?: 0) > 0 }
        val cardGoals = goals.map { goal ->
            val log = logsByGoal[goal.id]
            CalendarGoalModel(
                id = goal.id,
                title = goal.title,
                description = humanize.goalDetails(
                    unit = GoalUnit.fromStorage(goal.unit),
                    amount = goal.amount,
                    repeat = goal.repeat,
                    weight = goal.weight,
                ).best,
                isDone = goal.isDone(log),
                actualAmount = log?.actualAmount,
                plannedAmount = goal.amount,
            )
        }
        return CalendarCardModel(
            date = date,
            entryId = entry.id,
            entryKind = kind,
            title = entry.title,
            timeFrom = entry.toLocalTimeFrom(),
            timeTo = entry.toLocalTimeTo(),
            isSeries = entry.isSeries(),
            isAlarm = entry.isAlarm(),
            isVisited = isVisited,
            goals = cardGoals,
            goalsDone = cardGoals.count { it.isDone },
            goalsTotal = cardGoals.size,
        )
    }
}