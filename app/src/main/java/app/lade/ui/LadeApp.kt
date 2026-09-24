package app.lade.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import app.lade.draft.DraftApi
import app.lade.navigation.AppNavGraph
import app.lade.navigation.Routes
import app.lade.ui.profile.ProfileSheet

@Composable
fun LadeApp(
    draftApi: DraftApi,
    deepLink: String? = null,
) {
    val navController = rememberNavController()


    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            navController.navigate(deepLink) {
                launchSingleTop = true
            }
        }
    }

    var showProfileSheet by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
            AppNavGraph(
                navController = navController,
                draftApi = draftApi,
                onOpenProfile = { showProfileSheet = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }

    if (showProfileSheet) {
        ProfileSheet(
            onDismiss = { showProfileSheet = false },
            onOpenSettings = {
                showProfileSheet = false
                navController.navigate(Routes.Settings)
            },
        )
    }
}