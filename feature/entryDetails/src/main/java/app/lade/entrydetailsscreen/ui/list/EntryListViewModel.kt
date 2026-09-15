package app.lade.entrydetailsscreen.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryModel
import app.lade.entrydetailsscreen.domain.EntryListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class EntryListViewModel @Inject constructor(
	private val agendaApi: AgendaApi,
) : ViewModel() {
	private val kindFilter = MutableStateFlow<EntryKind?>(null)
	private val showArchived = MutableStateFlow(false)

	val state: StateFlow<EntryListUiState> = combine(
		agendaApi.observeAllEntries(),
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
			agendaApi.archiveEntry(entryId)
		}
	}

	fun restore(entryId: Long) {
		viewModelScope.launch {
			val entry = agendaApi.get(entryId, LocalDate.now())?.entry ?: return@launch
			agendaApi.saveEntry(entry.copy(archivedAtEpochMs = null))
		}
	}
}