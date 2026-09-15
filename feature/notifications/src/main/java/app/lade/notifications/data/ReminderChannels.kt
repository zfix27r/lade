package app.lade.notifications.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import app.lade.notifications.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderChannels @Inject constructor(
    @ApplicationContext private val context: Context,
) {
	fun ensureCreated() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
		val manager = context.getSystemService(NotificationManager::class.java) ?: return
		manager.createNotificationChannel(
			NotificationChannel(
                CHANNEL_SOFT,
                context.getString(R.string.reminders_channel_soft_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
				description = context.getString(R.string.reminders_channel_soft_desc)
			},
		)
		manager.createNotificationChannel(
			NotificationChannel(
                CHANNEL_ALARM,
                context.getString(R.string.reminders_channel_alarm_name),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
				description = context.getString(R.string.reminders_channel_alarm_desc)
				setBypassDnd(true)
			},
		)
	}

	fun areNotificationsEnabled(): Boolean =
		NotificationManagerCompat.from(context).areNotificationsEnabled()

	companion object {
		const val CHANNEL_SOFT = "lade_reminders_soft"
		const val CHANNEL_ALARM = "lade_reminders_alarm"
	}
}