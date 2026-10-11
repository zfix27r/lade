package app.lade.calendardata.api

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarDataApi {
    fun observeList(date: LocalDate): Flow<List<CalendarCardModel>>
    fun observeRange(from: LocalDate, to: LocalDate): Flow<List<CalendarCardModel>>
    fun observeMarkedDates(from: LocalDate, to: LocalDate): Flow<Map<LocalDate, DayProgress>>
    suspend fun get(entryId: Long, date: LocalDate): CalendarCardModel?

    suspend fun getGoalDetails(
        entryId: Long,
        date: LocalDate,
    ): List<CalendarGoalExpandedModel>

    suspend fun getTimerHistory(
        entryId: Long,
        date: LocalDate,
    ): List<CalendarTimerHistoryItem>

    suspend fun toggleGoal(entryId: Long, date: LocalDate, goalId: Long)
    suspend fun toggleAllGoals(entryId: Long, date: LocalDate)
    suspend fun skipAllGoals(entryId: Long, date: LocalDate)

    suspend fun setGoalAmount(
        entryId: Long,
        date: LocalDate,
        goalId: Long,
        actualAmount: Int,
    )

    suspend fun markEventVisited(entryId: Long, date: LocalDate, visited: Boolean)

    suspend fun startTimer(entryId: Long, date: LocalDate, startedAtMs: Long)
    suspend fun finishTimer(
        entryId: Long,
        date: LocalDate,
        endedAtMs: Long,
        actualMinutes: Int,
    )
}