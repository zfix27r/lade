package app.lade.time.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.categories.domain.CategoryRepository
import app.lade.categories.domain.model.Category
import app.lade.temporal.ui.TemporalOptions
import app.lade.time.domain.SaveManualTimeBlock
import app.lade.time.domain.TimeBlockRepository
import app.lade.time.domain.model.TimeBlockSource
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

data class TimeBlockEditUiState(
	val id: Long = 0,
	val title: String = "",
	val categoryId: Long? = null,
	val temporal: TemporalOptions = TemporalOptions(
		date = LocalDate.now(),
		time = LocalTime.of(9, 0),
		timeEnd = LocalTime.of(10, 0),
	),
	val isNew: Boolean = true,
	val isManual: Boolean = true,
	val saved: Boolean = false,
	val deleted: Boolean = false,
	val error: Boolean = false,
)

@HiltViewModel
class TimeBlockEditViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val blockRepository: TimeBlockRepository,
	private val saveManualTimeBlock: SaveManualTimeBlock,
	categoryRepository: CategoryRepository,
) : ViewModel() {
	private val blockId: Long = savedStateHandle.get<Long>("blockId") ?: -1L
	private val dateEpochDay: Long = savedStateHandle.get<Long>("dateEpochDay")
		?: LocalDate.now().toEpochDay()

	val categories: StateFlow<List<Category>> = categoryRepository.observeActive()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	private val _state = MutableStateFlow(
		TimeBlockEditUiState(
			isNew = blockId < 0,
			temporal = TemporalOptions(
				date = LocalDate.ofEpochDay(dateEpochDay),
				time = LocalTime.of(9, 0),
				timeEnd = LocalTime.of(10, 0),
			),
		),
	)
	val state: StateFlow<TimeBlockEditUiState> = _state.asStateFlow()

	init {
		if (blockId >= 0) {
			viewModelScope.launch {
				blockRepository.getById(blockId)?.let { block ->
					_state.value = TimeBlockEditUiState(
						id = block.id,
						title = block.title.orEmpty(),
						categoryId = block.categoryId,
						temporal = TemporalOptions(
							date = block.date,
							time = block.start,
							timeEnd = block.end,
						),
						isNew = false,
						isManual = block.source == TimeBlockSource.MANUAL,
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

	fun onTitleChange(value: String) = _state.update { it.copy(title = value, error = false) }
	fun onCategoryChange(id: Long) = _state.update { it.copy(categoryId = id, error = false) }
	fun onTemporalChange(value: TemporalOptions) = _state.update { it.copy(temporal = value, error = false) }

	fun save() {
		val current = _state.value
		if (!current.isManual) return
		val categoryId = current.categoryId ?: return
		val date = current.temporal.date ?: return
		val start = current.temporal.time ?: return
		val end = current.temporal.timeEnd ?: return
		if (!end.isAfter(start)) return
		viewModelScope.launch {
			try {
				saveManualTimeBlock.save(
					id = current.id,
					date = date,
					start = start,
					end = end,
					categoryId = categoryId,
					title = current.title,
				)
				_state.update { it.copy(saved = true, error = false) }
			} catch (_: IllegalArgumentException) {
				_state.update { it.copy(error = true) }
			}
		}
	}

	fun delete() {
		val current = _state.value
		if (current.isNew || !current.isManual) return
		viewModelScope.launch {
			saveManualTimeBlock.delete(current.id)
			_state.update { it.copy(deleted = true) }
		}
	}
}
