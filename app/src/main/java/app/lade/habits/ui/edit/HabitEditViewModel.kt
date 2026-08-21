package app.lade.habits.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.categories.domain.CategoryRepository
import app.lade.categories.domain.model.Category
import app.lade.habits.domain.HabitRepository
import app.lade.habits.domain.model.Habit
import app.lade.temporal.domain.RecurrenceDraft
import app.lade.temporal.ui.AlarmModeOption
import app.lade.temporal.ui.TemporalOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class HabitEditUiState(
	val id: Long = 0,
	val title: String = "",
	val categoryId: Long? = null,
	val goalValueText: String = "2",
	val goalUnit: String = "km",
	val temporal: TemporalOptions = TemporalOptions(
		recurrence = RecurrenceDraft(),
	),
	val isNew: Boolean = true,
	val saved: Boolean = false,
)

@HiltViewModel
class HabitEditViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val habitRepository: HabitRepository,
	categoryRepository: CategoryRepository,
) : ViewModel() {
	private val habitId: Long = savedStateHandle.get<Long>("habitId") ?: -1L

	val categories: StateFlow<List<Category>> = categoryRepository.observeActive()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	private val _state = MutableStateFlow(HabitEditUiState(isNew = habitId < 0))
	val state: StateFlow<HabitEditUiState> = _state.asStateFlow()

	init {
		if (habitId >= 0) {
			viewModelScope.launch {
				habitRepository.getById(habitId)?.let { habit ->
					_state.value = HabitEditUiState(
						id = habit.id,
						title = habit.title,
						categoryId = habit.categoryId,
						goalValueText = habit.goalValue.toString(),
						goalUnit = habit.goalUnit,
						temporal = TemporalOptions(
							time = habit.timeOfDayMinutes?.let { LocalTime.ofSecondOfDay(it * 60L) },
							recurrence = RecurrenceDraft.fromRrule(habit.rrule),
							alarmMode = AlarmModeOption.fromStorage(habit.alarmMode),
							reminderMinutesBefore = habit.reminderMinutesBefore,
						),
						isNew = false,
					)
				}
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

	fun save() {
		val current = _state.value
		val categoryId = current.categoryId ?: return
		val goal = current.goalValueText.replace(',', '.').toDoubleOrNull() ?: return
		val rrule = current.temporal.recurrence.toRrule()
		if (current.title.isBlank() || rrule.isBlank()) return
		if (!current.temporal.recurrence.hasValidDays()) {
			return
		}
		viewModelScope.launch {
			habitRepository.save(
				Habit(
					id = current.id,
					title = current.title.trim(),
					categoryId = categoryId,
					rrule = rrule,
					goalValue = goal,
					goalUnit = current.goalUnit,
					goalType = when (current.goalUnit) {
						"min" -> "duration"
						"reps" -> "count"
						else -> "distance"
					},
					timeOfDayMinutes = current.temporal.time?.let { it.hour * 60 + it.minute },
					alarmMode = current.temporal.alarmMode.storage,
					reminderMinutesBefore = current.temporal.reminderMinutesBefore,
				),
			)
			_state.update { it.copy(saved = true) }
		}
	}
}
