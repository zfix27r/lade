package app.lade.agenda.api

import app.lade.agenda.api.log.LogActualModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AgendaProvider {
    suspend fun get(entryId: Long, date: LocalDate): AgendaModel?
    suspend fun getList(date: LocalDate): List<AgendaModel>
    fun observeList(date: LocalDate): Flow<List<AgendaModel>>
    suspend fun markDay(entryId: Long, date: LocalDate, actuals: Map<Long, LogActualModel>)
}