package app.lade.schedule.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.resources.R

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

