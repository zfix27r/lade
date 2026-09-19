package app.lade.entrydetailsscreen.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.entrydetailsscreen.data.EntryListRepository
import app.lade.entrydetailsscreen.domain.EntryListUiState
import app.lade.entrykind.EntryKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryListViewModel @Inject constructor(
	private val repository: EntryListRepository,
) : ViewModel() {

	private val kindFilter = MutableStateFlow<EntryKind?>(null)
	private val showArchived = MutableStateFlow(false)

	val state: StateFlow<EntryListUiState> = combine(
		repository.observeAll(),
		repository.observeArchived(),
		kindFilter,
		showArchived,
	) { all, archived, kind, archivedOnly ->
		val source = if (archivedOnly) archived else all.filter { !it.isArchived }
		val filtered = source.filter { entry ->
			kind == null || entry.kind == kind
		}
		EntryListUiState(items = filtered, kindFilter = kind, showArchived = archivedOnly)
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EntryListUiState())

	fun setKindFilter(kind: EntryKind?) = kindFilter.update { kind }

	fun setShowArchived(value: Boolean) = showArchived.update { value }

	fun archive(entryId: Long) {
		viewModelScope.launch { repository.archive(entryId) }
	}

	fun restore(entryId: Long) {
		viewModelScope.launch { repository.restore(entryId) }
	}
}