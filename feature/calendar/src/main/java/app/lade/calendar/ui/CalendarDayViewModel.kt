package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.log.LogOrigin
import app.lade.agenda.api.log.LogSaveGoalModel
import app.lade.agenda.api.log.LogSaveModel
import app.lade.calendar.domain.CalendarDayBusy
import app.lade.calendar.domain.DueHabitItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarDayViewModel @Inject constructor(
	private val agendaApi: AgendaApi,
	private val dayPlanMapper: CalendarDayPlanMapper,
) : ViewModel() {
	private val selectedDate = MutableStateFlow(LocalDate.now())

	val date: StateFlow<LocalDate> = selectedDate

	private val agendas: StateFlow<List<AgendaModel>> = selectedDate
		.flatMapLatest { date -> agendaApi.observeList(date) }
		.stateIn(
			viewModelScope,
			SharingStarted.WhileSubscribed(5_000),
			emptyList(),
		)

	val dayBusy: StateFlow<CalendarDayBusy> = agendas
		.map { list -> dayPlanMapper.toDayBusy(selectedDate.value, list) }
		.stateIn(
			viewModelScope,
			SharingStarted.WhileSubscribed(5_000),
			CalendarDayBusy(LocalDate.now(), emptyList(), Duration.ZERO, Duration.ofHours(24)),
		)

	val dueHabits: StateFlow<List<DueHabitItem>> = agendas
		.map { list -> dayPlanMapper.habitItems(list) }
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	fun selectDate(date: LocalDate) {
		selectedDate.value = date
	}

	fun goToday() {
		selectedDate.value = LocalDate.now()
	}

	fun goPreviousDay() {
		selectedDate.value = selectedDate.value.minusDays(1)
	}

	fun goNextDay() {
		selectedDate.value = selectedDate.value.plusDays(1)
	}

	fun markDone(entryId: Long) = mark(entryId, done = true)

	fun markSkip(entryId: Long) = mark(entryId, done = false)

	private fun mark(entryId: Long, done: Boolean) {
		val date = selectedDate.value
		viewModelScope.launch {
			val agenda = agendas.value.find { it.entry.id == entryId } ?: return@launch
			val goals = agenda.goals.map { goal ->
				LogSaveGoalModel(
					goalId = goal.id,
					amount = if (done) goal.amount else 0,
					repeat = if (done) goal.repeat else 0,
					weight = goal.weight,
				)
			}
			val saveModel = LogSaveModel(
				date = date,
				goals = goals,
				origin = LogOrigin.CALENDAR,
			)
			agendaApi.saveLogs(saveModel)
		}
	}
}