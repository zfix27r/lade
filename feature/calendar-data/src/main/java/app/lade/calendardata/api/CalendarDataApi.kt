package app.lade.calendardata.api

import app.lade.calendardata.api.agenda.CalendarDataDayModel
import app.lade.calendardata.api.agenda.CalendarDataGoalModel
import app.lade.calendardata.api.mark.CalendarDataMarkModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarDataApi {
    fun observeRangeDays(from: LocalDate, to: LocalDate): Flow<List<CalendarDataDayModel>>
    fun observeMarkedDates(
        from: LocalDate,
        to: LocalDate
    ): Flow<Map<LocalDate, CalendarDataMarkModel>>

    suspend fun getGoalDetails(entryId: Long, date: LocalDate): List<CalendarDataGoalModel>

    suspend fun toggleGoal(entryId: Long, date: LocalDate, goalId: Long)
    suspend fun toggleAllGoals(entryId: Long, date: LocalDate)
    suspend fun skipAllGoals(entryId: Long, date: LocalDate)
    suspend fun setGoalAmount(entryId: Long, date: LocalDate, goalId: Long, actualAmount: Int)

    suspend fun startTimer(entryId: Long, date: LocalDate, startedAtMs: Long)
    suspend fun finishTimer(entryId: Long, date: LocalDate, endedAtMs: Long, actualMinutes: Int)
}