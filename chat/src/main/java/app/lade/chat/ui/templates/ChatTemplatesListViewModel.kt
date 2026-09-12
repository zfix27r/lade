package app.lade.chat.ui.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.chat.domain.ChatCorpusRepository
import app.lade.chat.domain.ChatDictRepository
import app.lade.chat.domain.ChatHistoryRepository
import app.lade.chat.domain.model.ChatDictEntry
import app.lade.chat.domain.model.CorpusEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatTemplatesListViewModel @Inject constructor(
	private val dictRepository: ChatDictRepository,
	private val corpusRepository: ChatCorpusRepository,
	private val historyRepository: ChatHistoryRepository,
) : ViewModel() {
	val systemCorpus: StateFlow<List<CorpusEntry>> = corpusRepository.observeSystem()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	val userDicts: StateFlow<List<ChatDictEntry>> = dictRepository.observeActive()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	fun archiveDict(id: Long) {
		viewModelScope.launch { dictRepository.archive(id) }
	}

	fun reloadSystemCorpus() {
		viewModelScope.launch { corpusRepository.reload() }
	}

	fun clearChatHistory() {
		viewModelScope.launch { historyRepository.clear() }
	}
}
