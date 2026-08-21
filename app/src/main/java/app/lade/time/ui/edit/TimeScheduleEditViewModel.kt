package app.lade.time.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.categories.domain.CategoryRepository
import app.lade.categories.domain.model.Category
import app.lade.temporal.domain.DaysOfWeekFlags
import app.lade.temporal.domain.RecurrenceDraft
import app.lade.temporal.domain.RecurrencePreset
import app.lade.temporal.ui.TemporalOptions
import app.lade.time.domain.ExpandTimeSchedule
import app.lade.time.domain.TimeScheduleRepository
import app.lade.time.domain.model.TimeSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class TimeScheduleEditUiState(
	val id: Long = 0,
	val title: String = "",
	val categoryId: Long? = null,
	val temporal: TemporalOptions = TemporalOptions(
		dateFrom = LocalDate.now(),
		dateTo = LocalDate.now().plusMonths(1),
		time = LocalTime.of(9, 0),
		timeEnd = LocalTime.of(18, 0),
		recurrence = RecurrenceDraft(RecurrencePreset.Weekdays, daysOfWeek = DaysOfWeekFlags.WEEKDAYS),
	),
	val isNew: Boolean = true,
	val saved: Boolean = false,
)

@HiltViewModel
class TimeScheduleEditViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val scheduleRepository: TimeScheduleRepository,
	private val expandTimeSchedule: ExpandTimeSchedule,
	categoryRepository: CategoryRepository,
) : ViewModel() {
	private val scheduleId: Long = savedStateHandle.get<Long>("scheduleId") ?: -1L

	val categories: StateFlow<List<Category>> = categoryRepository.observeActive()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	private val _state = MutableStateFlow(TimeScheduleEditUiState(isNew = scheduleId < 0))
	val state: StateFlow<TimeScheduleEditUiState> = _state.asStateFlow()

	init {
		if (scheduleId >= 0) {
			viewModelScope.launch {
				scheduleRepository.getById(scheduleId)?.let { schedule ->
					_state.value = TimeScheduleEditUiState(
						id = schedule.id,
						title = schedule.title,
						categoryId = schedule.categoryId,
						temporal = TemporalOptions(
							dateFrom = schedule.dateFrom,
							dateTo = schedule.dateTo,
							time = schedule.startTime,
							timeEnd = schedule.endTime,
							recurrence = RecurrenceDraft.fromRrule(schedule.rrule)
								.coercePreset(RecurrencePreset.SCHEDULE),
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
	fun onTemporalChange(value: TemporalOptions) = _state.update { it.copy(temporal = value) }

	fun save() {
		val current = _state.value
		val categoryId = current.categoryId ?: return
		val t = current.temporal
		val dateFrom = t.dateFrom ?: return
		val dateTo = t.dateTo ?: return
		val start = t.time ?: return
		val end = t.timeEnd ?: return
		val rrule = t.recurrence.toRrule()
		if (current.title.isBlank() ||
			!t.recurrence.hasValidDays() ||
			rrule.isBlank() ||
			!end.isAfter(start) ||
			dateTo.isBefore(dateFrom)
		) {
			return
		}
		viewModelScope.launch {
			val schedule = TimeSchedule(
				id = current.id,
				title = current.title.trim(),
				categoryId = categoryId,
				dateFrom = dateFrom,
				dateTo = dateTo,
				rrule = rrule,
				startTime = start,
				endTime = end,
			)
			val id = scheduleRepository.save(schedule)
			expandTimeSchedule.sync(schedule.copy(id = id))
			_state.update { it.copy(saved = true) }
		}
	}
}
