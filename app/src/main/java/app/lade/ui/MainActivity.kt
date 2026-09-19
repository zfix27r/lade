package app.lade.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.lade.navigation.Routes
import app.lade.ui.theme.LadeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			LadeTheme {
				Surface(modifier = Modifier.fillMaxSize()) {
					LadeApp(
						deepLink = handleDeepLink(intent),
					)
				}
			}
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		setIntent(intent)
	}

	private fun handleDeepLink(intent: Intent?): String? {
		if (intent?.action == Intent.ACTION_VIEW) {
			val uri = intent.data
			if (uri?.scheme == "lade" && uri.host == "open") {
				return Routes.Calendar
			}
		}
		return null
	}
}