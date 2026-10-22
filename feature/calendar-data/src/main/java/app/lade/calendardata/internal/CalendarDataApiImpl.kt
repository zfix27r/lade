package app.lade.calendardata.internal

import app.lade.agendastore.entry.EntryDao
import app.lade.agendastore.entry.EntryEntity
import app.lade.agendastore.goal.GoalDao
import app.lade.agendastore.goal.GoalEntity
import app.lade.agendastore.log.LogDao
import app.lade.agendastore.log.LogEntity
import app.lade.calendardata.api.CalendarDataApi
import app.lade.calendardata.api.models.CalendarDataModel
import app.lade.calendardata.api.models.CalendarDataEntryModel
import app.lade.calendardata.api.models.CalendarDataGoalModel
import app.lade.calendardata.api.models.CalendarDataGoalsModel
import app.lade.calendardata.api.models.CalendarDataTimerModel
import app.lade.calendardata.internal.mapper.isDone
import app.lade.calendardata.internal.mapper.toEntryKind
import app.lade.calendardata.internal.mapper.toLocalTimeFrom
import app.lade.calendardata.internal.mapper.toLocalTimeTo
import app.lade.entry.EntryKind
import app.lade.goal.GoalUnit
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
) : CalendarDataApi {

    override fun observeRangeDays(
        from: LocalDate,
        to: LocalDate,
    ): Flow<List<CalendarDataModel>> {
        val fromEpoch = from.toEpochDay()
        val toEpoch = to.toEpochDay()

        return combine(
            entryDao.observeActive(),
            goalDao.observeAll(),
            logDao.observeBetween(fromEpoch, toEpoch),
        ) { entries, goals, logs ->
            val goalsByEntry = goals.groupBy { it.entryId }
            val logsByEntry = logs.groupBy { it.entryId }
            val result = mutableListOf<CalendarDataModel>()
            var date = from
            while (!date.isAfter(to)) {
                val epochDay = date.toEpochDay()
                val agendas = entries.mapNotNull { entry ->
                    if (!projector.appliesTo(entry, date)) return@mapNotNull null
                    buildAgenda(
                        entry = entry,
                        goals = goalsByEntry[entry.id].orEmpty(),
                        logs = logsByEntry[entry.id].orEmpty()
                            .filter { it.epochDay == epochDay },
                        date = date,
                    )
                }
                result += CalendarDataModel(date = date, agendas = agendas)
                date = date.plusDays(1)
            }
            result
        }
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
        val allDone = goals.all { goal -> goal.isDone(logsByGoal[goal.id]) }
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

    override suspend fun setGoalAmount(
        entryId: Long,
        date: LocalDate,
        goalId: Long,
        actualAmount: Int,
    ) {
        val goals = goalDao.getByEntryId(entryId)
        val goal = goals.firstOrNull { it.id == goalId } ?: return
        val log = LogEntity(
            entryId = entryId,
            epochDay = date.toEpochDay(),
            goalId = goalId,
            name = goal.title,
            unit = goal.unit,
            plannedAmount = goal.amount,
            plannedRepeat = goal.repeat,
            plannedWeight = goal.weight,
            actualAmount = actualAmount.coerceAtLeast(0),
            actualRepeat = goal.repeat ?: 0,
            actualWeight = goal.weight,
            origin = "calendar",
            createdAtEpochMs = System.currentTimeMillis(),
        )
        logDao.deleteByGoalAndDay(goalId, date.toEpochDay())
        logDao.insertAll(listOf(log))
    }

    override suspend fun startTimer(entryId: Long, date: LocalDate, startedAtMs: Long) {
        val entry = entryDao.getById(entryId) ?: return
        val log = LogEntity(
            entryId = entryId,
            epochDay = date.toEpochDay(),
            goalId = null,
            name = entry.title,
            plannedAmount = entry.durationMinutes,
            timerStartedAtMs = startedAtMs,
            actualAmount = null,
            origin = "timer",
            createdAtEpochMs = System.currentTimeMillis(),
        )
        logDao.insertAll(listOf(log))
    }

    override suspend fun finishTimer(
        entryId: Long,
        date: LocalDate,
        endedAtMs: Long,
        actualMinutes: Int,
    ) {
        val logs = logDao.getByEntryAndDay(entryId, date.toEpochDay())
        val lastTimer = logs
            .filter { it.goalId == null && it.origin == "timer" && it.actualAmount == null }
            .maxByOrNull { it.createdAtEpochMs }
            ?: return
        logDao.insertAll(
            listOf(lastTimer.copy(actualAmount = actualMinutes.coerceAtLeast(0)))
        )
    }

    private fun buildAgenda(
        entry: EntryEntity,
        goals: List<GoalEntity>,
        logs: List<LogEntity>,
        date: LocalDate,
    ): CalendarDataFullAgendaModel? {
        val kind = entry.toEntryKind() ?: return null
        val logsByGoal = logs.filter { it.goalId != null }.associateBy { it.goalId }
        val cardGoals = goals.map { goal -> goal.toModel(logsByGoal[goal.id]) }
        val daysLeft = entry.dateToEpochDay
            ?.let { it - date.toEpochDay() }
            ?.takeIf { it >= 0 }
            ?.toInt()

        return CalendarDataFullAgendaModel(
            entry = CalendarDataEntryModel(
                id = entry.id,
                kind = kind,
                title = entry.title,
                timeFrom = entry.toLocalTimeFrom(),
                timeTo = entry.toLocalTimeTo(),
            ),
            goals = if (cardGoals.isEmpty()) null else CalendarDataGoalsModel(
                goals = cardGoals,
                done = cardGoals.count { it.isDone },
                total = cardGoals.size,
                isAllDone = cardGoals.isNotEmpty() && cardGoals.all { it.isDone },
            ),
            timer = buildTimer(entry, logs),
            notifications = null,
            daysLeft = daysLeft,
        )
    }

    private fun GoalEntity.toModel(log: LogEntity?): CalendarDataGoalModel =
        CalendarDataGoalModel(
            id = id,
            title = title,
            isDone = isDone(log),
            actualAmount = log?.actualAmount,
            plannedAmount = amount,
            unit = GoalUnit.fromStorage(unit),
            repeat = repeat,
            weight = weight,
        )

    private fun buildTimer(
        entry: EntryEntity,
        logs: List<LogEntity>,
    ): CalendarDataTimerModel? {
        if (entry.kind != EntryKind.HABIT.storage) return null
        val duration = entry.durationMinutes ?: return null
        val startMinutes = entry.startTimeMinutes ?: return null

        val lastTimer = logs
            .filter { it.goalId == null && it.origin == "timer" }
            .maxByOrNull { it.createdAtEpochMs }

        return CalendarDataTimerModel(
            plannedStartMinutes = startMinutes,
            durationMinutes = duration,
            startedAtMs = lastTimer?.timerStartedAtMs,
            actualMinutes = lastTimer?.actualAmount,
        )
    }
}