package app.lade.notifications.data

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderPermissions @Inject constructor(
    @ApplicationContext private val context: Context,
) {
	fun hasPostNotifications(): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
		return ContextCompat.checkSelfPermission(
			context,
			Manifest.permission.POST_NOTIFICATIONS,
		) == PackageManager.PERMISSION_GRANTED
	}

	fun canScheduleExactAlarms(): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
		val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return false
		return alarmManager.canScheduleExactAlarms()
	}

	fun exactAlarmSettingsIntent(): Intent =
		Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
			data = Uri.parse("package:${context.packageName}")
		}
}