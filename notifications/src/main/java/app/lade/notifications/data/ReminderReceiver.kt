package app.lade.notifications.data

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import app.lade.notifications.domain.ReminderFireMode
import app.lade.notifications.domain.ReminderKind
import app.lade.resources.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
	@Inject
    lateinit var channels: ReminderChannels

	override fun onReceive(context: Context, intent: Intent?) {
		if (intent?.action != ACTION_FIRE) return
		channels.ensureCreated()
		val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
			.ifBlank { context.getString(R.string.app_name) }
		val kind = intent.getStringExtra(EXTRA_KIND)
		val modeName = intent.getStringExtra(EXTRA_MODE) ?: ReminderFireMode.NOTIFICATION.name
		val mode = runCatching { ReminderFireMode.valueOf(modeName) }
			.getOrDefault(ReminderFireMode.NOTIFICATION)
		val entityId = intent.getLongExtra(EXTRA_ENTITY_ID, 0L)
		val epochDay = intent.getLongExtra(EXTRA_EPOCH_DAY, 0L)

		val contentTitle = when (kind) {
			ReminderKind.HABIT.name -> context.getString(R.string.reminders_notif_habit_title)
			ReminderKind.BLOCK.name -> context.getString(R.string.reminders_notif_block_title)
			else -> context.getString(R.string.app_name)
		}
		val channel = when (mode) {
			ReminderFireMode.ALARM -> ReminderChannels.CHANNEL_ALARM
			ReminderFireMode.NOTIFICATION -> ReminderChannels.CHANNEL_SOFT
		}
		val open = PendingIntent.getActivity(
			context,
			ReminderAlarmScheduler.requestCode(
				ReminderKind.valueOf(kind ?: ReminderKind.HABIT.name),
				entityId,
				epochDay,
			),
		Intent(Intent.ACTION_VIEW, "lade://open".toUri()).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		},
		PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
		)
		val builder = NotificationCompat.Builder(context, channel)
			.setSmallIcon(R.drawable.ic_notification)
			.setContentTitle(contentTitle)
			.setContentText(title)
			.setContentIntent(open)
			.setAutoCancel(true)
			.setCategory(
				if (mode == ReminderFireMode.ALARM) {
					NotificationCompat.CATEGORY_ALARM
				} else {
					NotificationCompat.CATEGORY_REMINDER
				},
			)
			.setPriority(
				if (mode == ReminderFireMode.ALARM) {
					NotificationCompat.PRIORITY_HIGH
				} else {
					NotificationCompat.PRIORITY_DEFAULT
				},
			)
		if (mode == ReminderFireMode.ALARM) {
			builder.setDefaults(NotificationCompat.DEFAULT_ALL)
		}
		val notifId = ReminderAlarmScheduler.requestCode(
			runCatching { ReminderKind.valueOf(kind ?: "") }.getOrDefault(ReminderKind.HABIT),
			entityId,
			epochDay,
		)
		if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
			NotificationManagerCompat.from(context).notify(notifId, builder.build())
		}
	}

	companion object {
		const val ACTION_FIRE = "app.lade.notifications.FIRE"
		const val EXTRA_KIND = "kind"
		const val EXTRA_ENTITY_ID = "entityId"
		const val EXTRA_EPOCH_DAY = "epochDay"
		const val EXTRA_TITLE = "title"
		const val EXTRA_MODE = "mode"
	}
}