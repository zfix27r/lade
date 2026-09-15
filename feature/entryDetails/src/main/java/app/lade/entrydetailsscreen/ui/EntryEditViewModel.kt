package app.lade.entrydetailsscreen.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.goal.GoalUnit
import app.lade.agenda.api.overlap.OverlapChoice
import app.lade.daypart.data.DayPartPreferences
import app.lade.entrydetailsscreen.domain.EntryEditUiState
import app.lade.notifications.Rescheduler
import app.lade.schedule.data.TemporalOptions
import app.lade.schedule.data.TemporalOptionsConfig
import app.lade.schedule.ui.AlarmModeOption
import app.lade.temporal.api.RecurrenceDraft
import app.lade.temporal.api.RecurrencePreset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EntryEditViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val agendaApi: AgendaApi,
	private val dayPartPreferences: DayPartPreferences,
	private val reminderRescheduler: Rescheduler,
) : ViewModel() {
	private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: -1L
	private val kindArg: String = savedStateHandle.get<String>("kind") ?: EntryKind.TASK.storage
	private val dateEpochDay: Long = savedStateHandle.get<Long>("dateEpochDay") ?: -1L
	private val titleHint: String = savedStateHandle.get<String>("titleHint").orEmpty()

	private val _state = MutableStateFlow(initialState())
	val state: StateFlow<EntryEditUiState> = _state.asStateFlow()

	init {
		if (entryId >= 0) {
			viewModelScope.launch {
				val date = if (dateEpochDay >= 0) LocalDate.ofEpochDay(dateEpochDay) else LocalDate.now()
				val agenda = agendaApi.get(entryId, date) ?: return@launch
				val entry = agenda.entry
				val goal = agenda.goals.firstOrNull()
				_state.value = EntryEditUiState(
					id = entry.id,
					kind = entry.kind,
					title = entry.title,
					goalValueText = goal?.amount?.toString() ?: "1",
					goalUnit = goal?.unit?.let { GoalUnit.fromStorage(it) } ?: GoalUnit.SET,
					temporal = TemporalOptions(
						date = entry.dateFrom,
						dateFrom = entry.dateFrom,
						dateTo = entry.dateTo,
						time = entry.startTime,
						timeEnd = entry.endTime,
						recurrence = entry.rrule?.let { RecurrenceDraft.fromRrule(it) } ?: RecurrenceDraft(),
						alarmMode = AlarmModeOption.fromStorage(entry.alarmMode),
						reminderMinutesBefore = entry.reminderMinutesBefore,
					),
					dayPartClock = dayPartPreferences.clock(),
					createdAtEpochMs = entry.createdAtEpochMs,
					isNew = false,
				)
			}
		}
	}

	fun onTitleChange(value: String) = _state.update { it.copy(title = value) }
	fun onGoalValueChange(value: String) = _state.update { it.copy(goalValueText = value) }
	fun onGoalUnitChange(unit: GoalUnit) = _state.update { it.copy(goalUnit = unit) }
	fun onTemporalChange(value: TemporalOptions) = _state.update { it.copy(temporal = value) }

	fun dismissMissingPrompt() = _state.update { it.copy(missingPrompt = null) }

	fun applyMissingTitle(title: String) {
		_state.update { it.copy(title = title.trim(), missingPrompt = null) }
		save()
	}

	fun dismissContainment() = _state.update {
		it.copy(containment = null, pendingCovering = null)
	}

	fun resolveContainmentChoice(choice: OverlapChoice) {
		val conflict = _state.value.containment ?: return
		val pending = _state.value.pendingCovering ?: return
		viewModelScope.launch {
			val result = agendaApi.resolveOverlap(choice, conflict, pending)
			when (result) {
				is Result.Success -> {
					reminderRescheduler.rescheduleAll()
					_state.update {
						it.copy(containment = null, pendingCovering = null, saved = true)
					}
				}
				is Result.Failure -> {
					_state.update { it.copy(containment = null, pendingCovering = null) }
				}
			}
		}
	}

	fun save() {
		val current = _state.value
		val entry = buildEntry(current)
		viewModelScope.launch {
			when (val result = agendaApi.saveEntry(entry)) {
				is Result.Success -> {
					val goals = buildGoals(current, result.value)
					if (goals.isNotEmpty()) {
						agendaApi.saveGoals(result.value, goals)
					}
					reminderRescheduler.rescheduleAll()
					_state.update {
						it.copy(
							containment = null,
							pendingCovering = null,
							missingPrompt = null,
							saved = true,
						)
					}
				}
				is Result.Failure -> {
					_state.update { it.copy(missingPrompt = result.error) }
				}
			}
		}
	}

	fun temporalConfig(): TemporalOptionsConfig {
		val kind = _state.value.kind
		val clock = _state.value.dayPartClock
		return when (kind) {
			EntryKind.TASK -> TemporalOptionsConfig(
				showDate = true,
				showTime = true,
				showRecurrence = true,
				recurrencePresets = RecurrencePreset.HABIT,
				showAlarm = true,
				showReminder = true,
				useSectionCards = true,
				dayPartClock = clock,
			)
			EntryKind.EVENT -> TemporalOptionsConfig(
				showDate = true,
				showTimeRange = true,
				showRecurrence = true,
				recurrencePresets = RecurrencePreset.HABIT,
				showAlarm = true,
				showReminder = true,
				useSectionCards = true,
			)
			EntryKind.HABIT -> TemporalOptionsConfig(
				showTime = true,
				showRecurrence = true,
				recurrencePresets = RecurrencePreset.HABIT,
				showAlarm = true,
				showReminder = true,
				useSectionCards = true,
				dayPartClock = clock,
			)
			EntryKind.SCHEDULE -> TemporalOptionsConfig(
				showDateRange = true,
				showTimeRange = true,
				showRecurrence = true,
				recurrencePresets = RecurrencePreset.SCHEDULE,
				useSectionCards = true,
			)
			EntryKind.UNKNOWN -> TemporalOptionsConfig()
		}
	}

	private fun buildEntry(state: EntryEditUiState): EntryModel {
		val t = state.temporal
		val date = t.date ?: t.dateFrom
		val rrule = when (state.kind) {
			EntryKind.TASK, EntryKind.EVENT -> t.recurrence.toRrule().ifBlank { null }
			EntryKind.HABIT, EntryKind.SCHEDULE -> t.recurrence.toRrule()
			EntryKind.UNKNOWN -> null
		}
		return EntryModel(
			id = state.id,
			kind = state.kind,
			title = state.title.trim(),
			dateFrom = when (state.kind) {
				EntryKind.SCHEDULE -> t.dateFrom ?: date
				else -> date
			},
			dateTo = when (state.kind) {
				EntryKind.SCHEDULE, EntryKind.TASK -> t.dateTo
				else -> null
			},
			startTime = t.time,
			endTime = t.timeEnd,
			rrule = rrule,
			alarmMode = t.alarmMode.storage,
			reminderMinutesBefore = t.reminderMinutesBefore,
			createdAtEpochMs = state.createdAtEpochMs,
		)
	}

	private fun buildGoals(state: EntryEditUiState, entryId: Long): List<GoalModel> {
		if (state.kind != EntryKind.HABIT) return emptyList()
		val target = state.goalValueText.replace(',', '.').toIntOrNull() ?: 1
		return listOf(
			GoalModel(
				entryId = entryId,
				title = "goal",
				unit = state.goalUnit.storage,
				amount = target,
			),
		)
	}

	private fun initialState(): EntryEditUiState {
		val kind = EntryKind.entries.find { it.storage == kindArg } ?: EntryKind.TASK
		val anchor = if (dateEpochDay >= 0) LocalDate.ofEpochDay(dateEpochDay) else LocalDate.now()
		return EntryEditUiState(
			kind = kind,
			title = titleHint.trim(),
			dayPartClock = dayPartPreferences.clock(),
			isNew = entryId < 0,
		)
	}
}