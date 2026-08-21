package app.lade.time.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.time.domain.TimeScheduleRepository
import app.lade.time.domain.model.TimeSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeSchedulesListViewModel @Inject constructor(
	private val repository: TimeScheduleRepository,
) : ViewModel() {
	val schedules: StateFlow<List<TimeSchedule>> = repository.observeActive()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	fun delete(id: Long) {
		viewModelScope.launch { repository.delete(id) }
	}
}
