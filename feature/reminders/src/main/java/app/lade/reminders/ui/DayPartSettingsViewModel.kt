package app.lade.reminders.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.daypart.data.DayPartPreferences
import app.lade.daypart.domain.DayPart
import app.lade.daypart.domain.DayPartClock
import app.lade.notifications.Rescheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DayPartSettingsViewModel @Inject constructor(
	private val dayPartPreferences: DayPartPreferences,
	private val reminderRescheduler: Rescheduler,
) : ViewModel() {
	private val _clock = MutableStateFlow(dayPartPreferences.clock())
	val clock: StateFlow<DayPartClock> = _clock.asStateFlow()

	fun setTime(part: DayPart, time: LocalTime) {
		dayPartPreferences.setTime(part, time)
		_clock.value = dayPartPreferences.clock()
		viewModelScope.launch {
			reminderRescheduler.rescheduleAll()
		}
	}
}
