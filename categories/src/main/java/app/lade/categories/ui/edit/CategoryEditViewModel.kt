package app.lade.categories.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.categories.domain.CategoryRepository
import app.lade.categories.domain.model.Category
import app.lade.categories.domain.model.CategoryColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryEditUiState(
	val id: Long = 0,
	val title: String = "",
	val color: String = "slate",
	val key: String? = null,
	val isNew: Boolean = true,
	val saved: Boolean = false,
)

@HiltViewModel
class CategoryEditViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val repository: CategoryRepository,
) : ViewModel() {
	private val categoryId: Long = savedStateHandle.get<Long>("categoryId") ?: -1L

	private val _state = MutableStateFlow(CategoryEditUiState(isNew = categoryId < 0))
	val state: StateFlow<CategoryEditUiState> = _state.asStateFlow()

	init {
		if (categoryId >= 0) {
			viewModelScope.launch {
				repository.getById(categoryId)?.let { category ->
					_state.value = CategoryEditUiState(
						id = category.id,
						title = category.title,
						color = category.color,
						key = category.key,
						isNew = false,
					)
				}
			}
		}
	}

	fun onTitleChange(value: String) = _state.update { it.copy(title = value) }

	fun onColorChange(value: String) = _state.update { it.copy(color = value) }

	fun save() {
		val current = _state.value
		if (current.title.isBlank()) return
		viewModelScope.launch {
			repository.save(
				Category(
					id = current.id,
					key = current.key,
					title = current.title.trim(),
					color = current.color.ifBlank { CategoryColors.ALL.first() },
				),
			)
			_state.update { it.copy(saved = true) }
		}
	}
}
