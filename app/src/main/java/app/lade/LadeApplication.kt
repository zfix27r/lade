package app.lade

import android.app.Application
import app.lade.database.seed.CategorySeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LadeApplication : Application() {
	@Inject lateinit var categorySeeder: CategorySeeder

	private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

	override fun onCreate() {
		super.onCreate()
		appScope.launch { categorySeeder.seedIfEmpty() }
	}
}
