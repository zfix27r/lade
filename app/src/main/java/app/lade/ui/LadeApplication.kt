package app.lade.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.lade.categories.data.seed.CategorySeeder
import app.lade.notifications.Rescheduler
import app.lade.notifications.data.ReminderChannels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LadeApplication : Application() {
	@Inject lateinit var categorySeeder: CategorySeeder
	@Inject lateinit var reminderChannels: ReminderChannels
	@Inject lateinit var reminderRescheduler: Rescheduler

	private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

	override fun onCreate() {
		super.onCreate()
		reminderChannels.ensureCreated()
		appScope.launch {
			categorySeeder.seedIfEmpty()
			reminderRescheduler.rescheduleAll()
		}
	}
}

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
	@Inject lateinit var rescheduler: Rescheduler

	override fun onReceive(context: Context, intent: Intent?) {
		if (intent?.action != Intent.ACTION_BOOT_COMPLETED &&
			intent?.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
		) {
			return
		}
		val pending = goAsync()
		CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
			try {
				rescheduler.rescheduleAll()
			} finally {
				pending.finish()
			}
		}
	}
}