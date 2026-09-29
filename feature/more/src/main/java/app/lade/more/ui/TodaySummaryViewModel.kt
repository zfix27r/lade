package app.lade.more.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.entry.EntryKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class TodaySummaryViewModel @Inject constructor(
	private val agendaApi: AgendaApi,
) : ViewModel() {
	private val today = LocalDate.now()

	val state: StateFlow<TodaySummaryUiState> = agendaApi
		.observeList(today)
		.map { agendas -> agendas.toUiState() }
		.stateIn(
			viewModelScope,
			SharingStarted.WhileSubscribed(5_000),
			TodaySummaryUiState(),
		)

	private fun List<AgendaModel>.toUiState(): TodaySummaryUiState {
		val open = filter { it.isOpen() }
		val habits = filter { it.entry.entryKind == EntryKind.HABIT }
		val habitDone = habits.count { it.isDone() }
		return TodaySummaryUiState(
			openCount = open.size,
			dueHabitTotal = habits.size,
			dueHabitDone = habitDone,
			previewTitles = open.take(3).map { it.entry.title },
		)
	}

	private fun AgendaModel.isOpen(): Boolean = when (entry.entryKind) {
		EntryKind.NOTE -> TODO()
		EntryKind.HABIT -> !isDone() && !isSkipped()
		EntryKind.TASK -> !isDone() && !isCancelled()
		EntryKind.EVENT, EntryKind.SCHEDULE -> true
	}

	private fun AgendaModel.isDone(): Boolean =
		logs.any { (it.actualAmount ?: 0) > 0 }

	private fun AgendaModel.isSkipped(): Boolean =
		logs.isEmpty() && entry.entryKind == EntryKind.HABIT

	private fun AgendaModel.isCancelled(): Boolean = false
}