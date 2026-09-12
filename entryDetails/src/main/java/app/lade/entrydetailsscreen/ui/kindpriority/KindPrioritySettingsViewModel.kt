package app.lade.entrydetailsscreen.ui.kindpriority

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.entry.domain.KindPriority
import app.lade.entry.domain.KindPriorityRepository
import app.lade.entry.domain.models.EntryKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KindPrioritySettingsViewModel @Inject constructor(
	private val kindPriorityRepository: KindPriorityRepository,
) : ViewModel() {
	val order: StateFlow<List<EntryKind>> = kindPriorityRepository.order
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), KindPriority.DEFAULT)

	fun moveUp(index: Int) {
		if (index <= 0) return
		reorder(index, index - 1)
	}

	fun moveDown(index: Int) {
		val current = order.value
		if (index >= current.lastIndex) return
		reorder(index, index + 1)
	}

	fun resetDefault() {
		viewModelScope.launch {
			kindPriorityRepository.setOrder(KindPriority.DEFAULT)
		}
	}

	private fun reorder(from: Int, to: Int) {
		val next = order.value.toMutableList()
		val item = next.removeAt(from)
		next.add(to, item)
		viewModelScope.launch {
			kindPriorityRepository.setOrder(next)
		}
	}
}
