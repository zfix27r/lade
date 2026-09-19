package app.lade.chat.ui.templates

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.chat.domain.ChatDictRepository
import app.lade.chat.domain.model.ChatDictEntry
import app.lade.entrykind.EntryKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatDictEditUiState(
	val id: Long = 0,
	val title: String = "",
	val kind: EntryKind = EntryKind.HABIT,
	val phrasesText: String = "",
	val isNew: Boolean = true,
	val saved: Boolean = false,
)

@HiltViewModel
class ChatDictEditViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val repository: ChatDictRepository,
) : ViewModel() {
	private val dictId: Long = savedStateHandle.get<Long>("dictId") ?: -1L

	private val _state = MutableStateFlow(ChatDictEditUiState(isNew = dictId < 0))
	val state: StateFlow<ChatDictEditUiState> = _state.asStateFlow()

	init {
		if (dictId >= 0) {
			viewModelScope.launch {
				repository.getById(dictId)?.let { d ->
					_state.value = ChatDictEditUiState(
						id = d.id,
						title = d.title,
						kind = d.kind,
						phrasesText = d.phrases.joinToString(", "),
						isNew = false,
					)
				}
			}
		}
	}

	fun onTitleChange(v: String) = _state.update { it.copy(title = v) }
	fun onPhrasesChange(v: String) = _state.update { it.copy(phrasesText = v) }
	fun onKindChange(v: EntryKind) = _state.update { it.copy(kind = v) }

	fun save() {
		val current = _state.value
		val phrases = current.phrasesText.split(',', ' ')
			.map { it.trim().lowercase() }
			.filter { it.isNotEmpty() }
		if (current.title.isBlank() || phrases.isEmpty()) return
		viewModelScope.launch {
			repository.save(
				ChatDictEntry(
					id = current.id,
					title = current.title.trim(),
					kind = current.kind,
					phrases = phrases,
				),
			)
			_state.update { it.copy(saved = true) }
		}
	}
}
