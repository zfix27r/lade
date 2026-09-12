package app.lade.entrydetailsscreen.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.categories.domain.CategoryRepository
import app.lade.categories.domain.model.Category
import app.lade.entrydetailsscreen.domain.ContainmentChoice
import app.lade.entrydetailsscreen.domain.ContainmentConflict
import app.lade.entrydetailsscreen.domain.DetectEntryContainment
import app.lade.entrydetailsscreen.domain.EntryCreateMatrix
import app.lade.entry.domain.EntryDayProjector
import app.lade.entry.domain.EntryHistoryRepository
import app.lade.entrydetailsscreen.domain.EntryMissingField
import app.lade.entry.domain.EntryRepository
import app.lade.entrydetailsscreen.domain.EntryTemporalDraft
import app.lade.entry.domain.ResolveContainment
import app.lade.entry.domain.model.Entry
import app.lade.entrydetailsscreen.domain.EntryGoalDef
import app.lade.entrydetailsscreen.domain.EntryKind
import app.lade.reminders.DayPartPreferences
import app.lade.reminders.ReminderRescheduler
import app.lade.temporal.domain.DayPartClock
import app.lade.temporal.domain.RecurrenceDraft
import app.lade.temporal.domain.RecurrencePreset
import app.lade.temporal.ui.AlarmModeOption
import app.lade.temporal.ui.TemporalOptions
import app.lade.temporal.ui.TemporalOptionsConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class EntryEditUiState(
	val id: Long = 0,
	val kind: EntryKind = EntryKind.TASK,
	val title: String = "",
	val categoryId: Long? = null,
	val goalValueText: String = "1",
	val goalUnit: String = "reps",
	val temporal: TemporalOptions = TemporalOptions(),
	val dayPartClock: DayPartClock = DayPartClock.DEFAULT,
	val createdAtEpochMs: Long = 0,
	val isNew: Boolean = true,
	val saved: Boolean = false,
	val missingPrompt: EntryMissingField? = null,
	val containment: ContainmentConflict? = null,
	val pendingCovering: Entry? = null,
)

@HiltViewModel
class EntryEditViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val entryRepository: EntryRepository,
	private val entryHistoryRepository: EntryHistoryRepository,
	private val entryDayProjector: EntryDayProjector,
	private val detectContainment: DetectEntryContainment,
	private val resolveContainment: ResolveContainment,
	private val categoryRepository: CategoryRepository,
	private val dayPartPreferences: DayPartPreferences,
	private val reminderRescheduler: ReminderRescheduler,
) : ViewModel() {
	private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: -1L
	private val kindArg: String = savedStateHandle.get<String>("kind") ?: EntryKind.TASK.storage
	private val dateEpochDay: Long = savedStateHandle.get<Long>("dateEpochDay") ?: -1L
	private val titleHint: String = savedStateHandle.get<String>("titleHint").orEmpty()

	val categories: StateFlow<List<Category>> = categoryRepository.observeActive()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	private val _state = MutableStateFlow(initialState())
	val state: StateFlow<EntryEditUiState> = _state.asStateFlow()

	init {
		if (entryId >= 0) {
			viewModelScope.launch {
				val entry = entryRepository.getById(entryId) ?: return@launch
				_state.value = EntryEditUiState(
					id = entry.id,
					kind = entry.kind,
					title = entry.title,
					categoryId = entry.categoryId,
					goalValueText = entry.goalDefs.firstOrNull()?.target?.let { formatGoal(it) } ?: "1",
					goalUnit = entry.goalDefs.firstOrNull()?.unit ?: "reps",
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
		viewModelScope.launch {
			categoryRepository.observeActive().collect { list ->
				if (_state.value.categoryId == null && list.isNotEmpty()) {
					_state.update { it.copy(categoryId = list.first().id) }
				}
			}
		}
	}

	fun onTitleChange(value: String) = _state.update { it.copy(title = value) }
	fun onCategoryChange(id: Long) = _state.update { it.copy(categoryId = id) }
	fun onGoalValueChange(value: String) = _state.update { it.copy(goalValueText = value) }
	fun onGoalUnitChange(unit: String) = _state.update { it.copy(goalUnit = unit) }
	fun onTemporalChange(value: TemporalOptions) = _state.update { it.copy(temporal = value) }

	fun createCategory(title: String, color: String) {
		viewModelScope.launch {
			val id = categoryRepository.save(Category(title = title.trim(), color = color))
			_state.update { it.copy(categoryId = id) }
		}
	}

	fun dismissMissingPrompt() = _state.update { it.copy(missingPrompt = null) }

	fun applyMissingTitle(title: String) {
		_state.update { it.copy(title = title.trim(), missingPrompt = null) }
		save()
	}

	fun dismissContainment() = _state.update {
		it.copy(containment = null, pendingCovering = null)
	}

	fun resolveContainmentChoice(choice: ContainmentChoice) {
		val conflict = _state.value.containment ?: return
		val pending = _state.value.pendingCovering ?: return
		viewModelScope.launch {
			when (choice) {
				ContainmentChoice.DELETE_COVERED -> {
					val coveredId = conflict.covered.entryId
					if (coveredId != 0L && coveredId != pending.id) {
						resolveContainment.apply(
							choice = choice,
							conflict = conflict,
							coveringEntry = pending,
							coveredEntry = entryRepository.getById(coveredId),
						)
					}
					if (pending.id == conflict.covered.entryId) {
						_state.update {
							it.copy(containment = null, pendingCovering = null, saved = true)
						}
					} else {
						persist(pending)
					}
				}
				ContainmentChoice.SPLIT_COVERING -> {
					val coveringEntry = when {
						conflict.covering.entryId == 0L || conflict.covering.entryId == pending.id ->
							pending
						else -> entryRepository.getById(conflict.covering.entryId) ?: return@launch
					}
					resolveContainment.apply(
						choice = choice,
						conflict = conflict,
						coveringEntry = coveringEntry,
						coveredEntry = null,
					)
					val pendingWasCovered = conflict.covered.entryId == pending.id ||
						(pending.id == 0L && conflict.covering.entryId != 0L &&
							conflict.covering.entryId == coveringEntry.id &&
							conflict.covered.start == pending.startTime)
					if (pendingWasCovered && coveringEntry.id != pending.id) {
						persist(pending)
					} else {
						reminderRescheduler.rescheduleAll()
						_state.update {
							it.copy(
								containment = null,
								pendingCovering = null,
								saved = true,
							)
						}
					}
				}
			}
		}
	}

	fun save() {
		val current = _state.value
		val draft = current.temporal.toDraft()
		val missing = EntryCreateMatrix.firstMissing(
			kind = current.kind,
			title = current.title,
			categoryId = current.categoryId,
			temporal = draft,
		)
		if (missing != null) {
			_state.update { it.copy(missingPrompt = missing) }
			return
		}
		val categoryId = current.categoryId ?: return
		val entry = buildEntry(current, categoryId)
		viewModelScope.launch {
			val start = entry.startTime
			val end = entry.endTime
			if (start != null && end != null && end > start) {
				val date = entry.dateFrom ?: LocalDate.now()
				val histories = entryHistoryRepository.observeByDate(date).first()
				val entries = entryRepository.observeActive().first()
				val plan = entryDayProjector.project(date, entries, histories)
				val conflicts = detectContainment.findConflictsAgainstDayPlan(
					candidateId = entry.id,
					candidateStart = start,
					candidateEnd = end,
					plan = plan,
				)
				val first = conflicts.firstOrNull()
				if (first != null) {
					_state.update {
						it.copy(containment = first, pendingCovering = entry)
					}
					return@launch
				}
			}
			persist(entry)
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
		}
	}

	private suspend fun persist(entry: Entry) {
		val toSave = if (entry.id == 0L && entry.createdAtEpochMs == 0L) {
			entry.copy(createdAtEpochMs = System.currentTimeMillis())
		} else {
			entry
		}
		entryRepository.save(toSave)
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

	private fun buildEntry(state: EntryEditUiState, categoryId: Long): Entry {
		val t = state.temporal
		val date = t.date ?: t.dateFrom
		val rrule = when (state.kind) {
			EntryKind.TASK, EntryKind.EVENT -> t.recurrence.toRrule().ifBlank { null }
			EntryKind.HABIT, EntryKind.SCHEDULE -> t.recurrence.toRrule()
		}
		val goals = if (state.kind == EntryKind.HABIT) {
			val target = state.goalValueText.replace(',', '.').toDoubleOrNull() ?: 1.0
			listOf(
				EntryGoalDef(
					key = "goal",
					unit = state.goalUnit,
					target = target,
				),
			)
		} else {
			emptyList()
		}
		return Entry(
			id = state.id,
			kind = state.kind,
			title = state.title.trim(),
			categoryId = categoryId,
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
			goalDefs = goals,
			alarmMode = t.alarmMode.storage,
			reminderMinutesBefore = t.reminderMinutesBefore,
			createdAtEpochMs = state.createdAtEpochMs,
		)
	}

	private fun initialState(): EntryEditUiState {
		val kind = EntryKind.fromStorage(kindArg)
		val anchor = if (dateEpochDay >= 0) LocalDate.ofEpochDay(dateEpochDay) else LocalDate.now()
		val draft = EntryCreateMatrix.defaultsFor(kind, anchor)
		return EntryEditUiState(
			kind = kind,
			title = titleHint.trim(),
			temporal = draft.toTemporal(),
			dayPartClock = dayPartPreferences.clock(),
			isNew = entryId < 0,
		)
	}

	private fun formatGoal(value: Double): String =
		if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
}

private fun TemporalOptions.toDraft() = EntryTemporalDraft(
	date = date,
	dateFrom = dateFrom,
	dateTo = dateTo,
	time = time,
	timeEnd = timeEnd,
	recurrence = recurrence,
)

private fun EntryTemporalDraft.toTemporal() = TemporalOptions(
	date = date,
	dateFrom = dateFrom,
	dateTo = dateTo,
	time = time,
	timeEnd = timeEnd,
	recurrence = recurrence,
)
