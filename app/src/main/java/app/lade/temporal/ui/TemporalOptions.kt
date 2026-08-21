package app.lade.temporal.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.R
import app.lade.temporal.domain.RecurrenceDraft
import app.lade.temporal.domain.RecurrencePreset
import java.time.LocalDate
import java.time.LocalTime

enum class AlarmModeOption(
	val storage: String,
	@StringRes val labelRes: Int,
) {
	None("none", R.string.alarm_mode_none),
	Notification("notification", R.string.alarm_mode_notification),
	Alarm("alarm", R.string.alarm_mode_alarm),
	;

	companion object {
		fun fromStorage(value: String?): AlarmModeOption =
			entries.find { it.storage == value } ?: None
	}
}

@Composable
fun AlarmModeOption.label(): String = stringResource(labelRes)

data class TemporalOptions(
	val date: LocalDate? = null,
	val time: LocalTime? = null,
	val timeEnd: LocalTime? = null,
	val dateFrom: LocalDate? = null,
	val dateTo: LocalDate? = null,
	val recurrence: RecurrenceDraft = RecurrenceDraft(),
	val alarmMode: AlarmModeOption = AlarmModeOption.None,
	val reminderMinutesBefore: Int? = null,
)

data class TemporalOptionsConfig(
	val showDate: Boolean = false,
	val showTime: Boolean = false,
	val showTimeRange: Boolean = false,
	val showDateRange: Boolean = false,
	val showRecurrence: Boolean = false,
	val recurrencePresets: List<RecurrencePreset> = RecurrencePreset.HABIT,
	val showAlarm: Boolean = false,
	val showReminder: Boolean = false,
)
