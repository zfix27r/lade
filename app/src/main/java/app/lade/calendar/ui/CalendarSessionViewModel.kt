package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.calendar.data.CalendarPreferences
import app.lade.calendar.data.CalendarSavedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarSessionViewModel @Inject constructor(
	private val preferences: CalendarPreferences,
) : ViewModel() {
	val savedState: StateFlow<CalendarSavedState?> = preferences.savedState.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		null,
	)

	fun persist(view: CalendarView, date: LocalDate) {
		viewModelScope.launch {
			preferences.save(view.name, date.toEpochDay())
		}
	}
}
