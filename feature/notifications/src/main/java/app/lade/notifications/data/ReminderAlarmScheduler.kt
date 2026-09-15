package app.lade.notifications.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import app.lade.notifications.domain.ReminderFireMode
import app.lade.notifications.domain.ReminderKind
import app.lade.notifications.domain.ReminderRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissions: ReminderPermissions,
) {
	private val alarmManager = context.getSystemService(AlarmManager::class.java)

	fun schedule(request: ReminderRequest) {
		val am = alarmManager ?: return
		val triggerAt = request.triggerAtEpochMs
		if (triggerAt <= System.currentTimeMillis()) return
		val pi = pendingIntent(request, PendingIntent.FLAG_UPDATE_CURRENT)
		when (request.mode) {
			ReminderFireMode.ALARM -> {
				if (permissions.canScheduleExactAlarms()) {
					am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
				} else {
					am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
				}
			}
			ReminderFireMode.NOTIFICATION -> {
				if (permissions.canScheduleExactAlarms()) {
					am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
				} else {
					am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
				}
			}
		}
	}

	fun cancel(kind: ReminderKind, entityId: Long, epochDay: Long) {
		val am = alarmManager ?: return
		val dummy = ReminderRequest(
            kind = kind,
            entityId = entityId,
            epochDay = epochDay,
            triggerAtEpochMs = 0L,
            title = "",
            mode = ReminderFireMode.NOTIFICATION,
        )
		am.cancel(pendingIntent(dummy, PendingIntent.FLAG_NO_CREATE) ?: return)
	}

	private fun pendingIntent(request: ReminderRequest, updateFlag: Int): PendingIntent {
		val intent = Intent(context, ReminderReceiver::class.java).apply {
			action = ReminderReceiver.ACTION_FIRE
			putExtra(ReminderReceiver.EXTRA_KIND, request.kind.name)
			putExtra(ReminderReceiver.EXTRA_ENTITY_ID, request.entityId)
			putExtra(ReminderReceiver.EXTRA_EPOCH_DAY, request.epochDay)
			putExtra(ReminderReceiver.EXTRA_TITLE, request.title)
			putExtra(ReminderReceiver.EXTRA_MODE, request.mode.name)
		}
		val flags = updateFlag or PendingIntent.FLAG_IMMUTABLE
		return PendingIntent.getBroadcast(
			context,
			requestCode(request.kind, request.entityId, request.epochDay),
			intent,
			flags,
		)
	}

	companion object {
		fun requestCode(kind: ReminderKind, entityId: Long, epochDay: Long): Int {
			var h = 17
			h = 31 * h + kind.ordinal
			h = 31 * h + entityId.hashCode()
			h = 31 * h + epochDay.hashCode()
			return h
		}
	}
}