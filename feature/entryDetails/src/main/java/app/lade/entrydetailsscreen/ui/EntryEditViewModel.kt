package app.lade.entrydetailsscreen.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.Result
import app.lade.agenda.api.agenda.AgendaError
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.agenda.AgendaSaveModel
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.agenda.api.overlap.OverlapModel
import app.lade.daypart.data.DayPartPreferences
import app.lade.entrydetailsscreen.domain.EntryEditRepository
import app.lade.entrydetailsscreen.domain.mapper.EntryEditMapper
import app.lade.entrydetailsscreen.domain.model.EntryEditEvent
import app.lade.entrydetailsscreen.domain.model.EntryEditUiState
import app.lade.entrydetailsscreen.domain.model.GoalDraft
import app.lade.entrydetailsscreen.domain.resolver.EntryKindResolver
import app.lade.entrykind.EntryKind
import app.lade.schedule.data.TemporalOptions
import app.lade.schedule.ui.AlarmModeOption
import app.lade.temporal.api.RecurrenceDraft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class EntryEditViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val repository: EntryEditRepository,
	private val resolver: EntryKindResolver,
	private val mapper: EntryEditMapper,
	private val dayPartPreferences: DayPartPreferences,
) : ViewModel() {

	private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: 0L
	private val dateEpochDay: Long = savedStateHandle.get<Long>("dateEpochDay") ?: -1L
	private val titleHint: String = savedStateHandle.get<String>("titleHint").orEmpty()

	private val _state = MutableStateFlow(initialState())
	val state: StateFlow<EntryEditUiState> = _state.asStateFlow()

	private val _events = Channel<EntryEditEvent>(Channel.BUFFERED)
	val events = _events.receiveAsFlow()

	private var original: EntryEditUiState = _state.value
	private var originalGoals: List<GoalModel> = emptyList()

	private var overlapConflict: OverlapModel? = null
	private var overlapPending: EntryModel? = null

	init {
		if (entryId > 0L) load(entryId)
	}

	// === Title ===

	fun onTitleChange(value: String) = updateState { it.copy(title = value) }

	// === Temporal (базовый) ===

	fun onTemporalChange(value: TemporalOptions) = updateState { it.copy(temporal = value) }

	// === Temporal (обёртки) ===

	fun onDateChange(date: LocalDate?) =
		onTemporalChange(_state.value.temporal.copy(dateFrom = date))

	fun onDateToChange(date: LocalDate?) =
		onTemporalChange(_state.value.temporal.copy(dateTo = date))

	fun onTimeChange(time: LocalTime?) =
		onTemporalChange(_state.value.temporal.copy(time = time))

	fun onTimeEndChange(time: LocalTime?) =
		onTemporalChange(_state.value.temporal.copy(timeEnd = time))

	fun onRecurrenceChange(draft: RecurrenceDraft) =
		onTemporalChange(_state.value.temporal.copy(recurrence = draft))

	fun onAlarmChange(mode: AlarmModeOption) =
		onTemporalChange(_state.value.temporal.copy(alarmMode = mode))

	fun onReminderChange(minutes: Int?) =
		onTemporalChange(_state.value.temporal.copy(reminderMinutesBefore = minutes))

	// === Goals ===

	fun onGoalChange(index: Int, draft: GoalDraft) = updateState { s ->
		s.copy(goals = s.goals.toMutableList().also { it[index] = draft })
	}

	fun onAddGoal() = updateState { s ->
		s.copy(goals = s.goals + GoalDraft(), goalsExplicitlyEnabled = true)
	}

	fun onRemoveGoal(index: Int) = updateState { s ->
		s.copy(goals = s.goals.toMutableList().also { it.removeAt(index) })
	}

	fun onGoalsEnabledChange(enabled: Boolean) = updateState { s ->
		s.copy(goalsExplicitlyEnabled = enabled)
	}

	// === Kind override ===

	fun onKindOverrideChange(kind: EntryKind?) = updateState { it.copy(kindOverride = kind) }

	// === Сохранение ===

	fun save() {
		val current = _state.value
		if (current.isSaving) return
		_state.update { it.copy(isSaving = true) }

		viewModelScope.launch {
			val entry = mapper.toEntryModel(current)
			val goals = mapper.toGoalModels(
				state = current,
				entryId = entry.id,
				base = originalGoals,
			)
			val model = AgendaSaveModel(entry = entry, goals = goals)

			when (val result = repository.save(model)) {
				is Result.Success -> {
					_state.update {
						it.copy(isSaving = false, isDirty = false, id = result.value, isNew = false)
					}
					original = _state.value
					_events.send(EntryEditEvent.Saved)
				}
				is Result.Failure -> when (val err = result.error) {
					is AgendaError.Overlap -> {
						overlapConflict = err.conflict
						overlapPending = err.pending
						_state.update { it.copy(isSaving = false) }
						_events.send(EntryEditEvent.ShowOverlap(err.conflict, err.pending))
					}
					else -> {
						_state.update { it.copy(isSaving = false) }
						_events.send(EntryEditEvent.ShowAgendaError(err))
					}
				}
			}
		}
	}

	// === Overlap ===

	fun resolveOverlap(choice: OverlapChoice) {
		val conflict = overlapConflict ?: return
		val pending = overlapPending ?: return
		viewModelScope.launch {
			when (val result = repository.resolveOverlap(choice, conflict, pending)) {
				is Result.Success -> {
					overlapConflict = null
					overlapPending = null
					_state.update { it.copy(isDirty = false) }
					original = _state.value
					_events.send(EntryEditEvent.Saved)
				}
				is Result.Failure -> {
					overlapConflict = null
					overlapPending = null
					_events.send(EntryEditEvent.ShowAgendaError(AgendaError.Unknown))
				}
			}
		}
	}

	fun dismissOverlap() {
		overlapConflict = null
		overlapPending = null
	}

	// === Back ===

	fun requestBack() {
		viewModelScope.launch {
			if (_state.value.isDirty) {
				_events.send(EntryEditEvent.ShowUnsavedChangesDialog)
			} else {
				_events.send(EntryEditEvent.Close)
			}
		}
	}

	fun confirmDiscard() {
		viewModelScope.launch { _events.send(EntryEditEvent.Close) }
	}

	// === Restore ===

	fun restore() {
		val id = _state.value.id
		if (id <= 0L) return
		viewModelScope.launch {
			when (repository.restore(id)) {
				is Result.Success -> _events.send(EntryEditEvent.Restored)
				is Result.Failure -> Unit
			}
		}
	}

	// === Загрузка ===

	private fun load(id: Long) {
		_state.update { it.copy(isLoading = true) }
		viewModelScope.launch {
			val date = if (dateEpochDay >= 0) LocalDate.ofEpochDay(dateEpochDay) else LocalDate.now()
			val data = repository.load(id, date)
			if (data == null) {
				_state.update { it.copy(isLoading = false) }
				return@launch
			}
			val loaded = mapper.toUiState(
				agenda = AgendaModel(date = date, entry = data.entry, goals = data.goals),
				isNew = false,
			)
			_state.value = loaded
			original = loaded
			originalGoals = data.goals
		}
	}

	// === Внутреннее ===

	private fun updateState(block: (EntryEditUiState) -> EntryEditUiState) {
		_state.update { current ->
			val next = block(current)
			val resolved = resolver.resolve(
				input = mapper.resolveKind(next),
				override = next.kindOverride,
			)
			val withResolved = next.copy(resolvedKind = resolved)
			withResolved.copy(isDirty = !isSameContent(withResolved, original))
		}
	}

	private fun isSameContent(a: EntryEditUiState, b: EntryEditUiState): Boolean =
		a.title == b.title &&
				a.temporal == b.temporal &&
				a.goals == b.goals &&
				a.goalsExplicitlyEnabled == b.goalsExplicitlyEnabled &&
				a.kindOverride == b.kindOverride &&
				a.effectiveKind == b.effectiveKind

	private fun initialState(): EntryEditUiState {
		val date = if (dateEpochDay >= 0) LocalDate.ofEpochDay(dateEpochDay) else LocalDate.now()
		return EntryEditUiState(
			title = titleHint.trim(),
			temporal = TemporalOptions(dateFrom = date),
			dayPartClock = dayPartPreferences.clock(),
			isNew = entryId <= 0L,
			resolvedKind = EntryKind.TASK,
			kindOverride = null,
		)
	}
}