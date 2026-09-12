package app.lade.entrydetailsscreen.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.model.Entry
import app.lade.entry.domain.model.EntryKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryListUiState(
	val items: List<Entry> = emptyList(),
	val kindFilter: EntryKind? = null,
	val showArchived: Boolean = false,
)

@HiltViewModel
class EntryListViewModel @Inject constructor(
	private val entryRepository: EntryRepository,
) : ViewModel() {
	private val kindFilter = MutableStateFlow<EntryKind?>(null)
	private val showArchived = MutableStateFlow(false)

	val state: StateFlow<EntryListUiState> = combine(
		entryRepository.observeAll(),
		kindFilter,
		showArchived,
	) { all, kind, archived ->
		val filtered = all.filter { entry ->
			val kindOk = kind == null || entry.kind == kind
			val archiveOk = if (archived) entry.isArchived else !entry.isArchived
			kindOk && archiveOk
		}
		EntryListUiState(items = filtered, kindFilter = kind, showArchived = archived)
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EntryListUiState())

	fun setKindFilter(kind: EntryKind?) = kindFilter.update { kind }

	fun setShowArchived(value: Boolean) = showArchived.update { value }

	fun archive(entryId: Long) {
		viewModelScope.launch {
			val entry = entryRepository.getById(entryId) ?: return@launch
			entryRepository.save(
				entry.copy(archivedAtEpochMs = System.currentTimeMillis()),
			)
		}
	}

	fun restore(entryId: Long) {
		viewModelScope.launch {
			val entry = entryRepository.getById(entryId) ?: return@launch
			entryRepository.save(entry.copy(archivedAtEpochMs = null))
		}
	}
}
