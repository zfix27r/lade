package app.lade.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.lade.navigation.AppNavGraph
import app.lade.navigation.Routes
import app.lade.resources.R

private data class BottomTab(
	val route: String,
	val labelRes: Int,
	val icon: ImageVector,
)

@Composable
fun LadeApp(
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

	val backStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute = backStackEntry?.destination?.route
	val showBottomBar = currentRoute in Routes.bottomBarRoutes
	val tabs = listOf(
		BottomTab(Routes.Calendar, R.string.nav_calendar, Icons.Default.CalendarMonth),
		BottomTab(Routes.Chat, R.string.nav_chat, Icons.AutoMirrored.Filled.Chat),
		BottomTab(Routes.More, R.string.nav_more, Icons.Default.MoreHoriz),
	)

	fun switchTab(route: String) {
		navController.navigate(route) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
			restoreState = true
		}
	}

	// Zero content insets: avoid double top gap under edge-to-edge (child TopAppBar owns status bars).
	// IME is handled by chat/create screens so the app bar stays put.
	Scaffold(
		contentWindowInsets = WindowInsets(0, 0, 0, 0),
		bottomBar = {
			if (showBottomBar) {
				NavigationBar {
					tabs.forEach { tab ->
						NavigationBarItem(
							selected = currentRoute == tab.route,
							onClick = { switchTab(tab.route) },
							icon = { Icon(tab.icon, contentDescription = null) },
							label = { Text(stringResource(tab.labelRes)) },
						)
					}
				}
			}
		},
	) { padding ->
		val layoutDirection = LocalLayoutDirection.current
		val navHostModifier = if (currentRoute == Routes.Chat && showBottomBar) {
			Modifier
				.fillMaxSize()
				.padding(
					PaddingValues(
						top = padding.calculateTopPadding(),
						start = padding.calculateStartPadding(layoutDirection),
						end = padding.calculateEndPadding(layoutDirection),
						bottom = 0.dp,
					),
				)
		} else {
			Modifier.fillMaxSize().padding(padding)
		}

		AppNavGraph(
			navController = navController,
			bottomNavHeight = padding.calculateBottomPadding(),
			switchTab = ::switchTab,
			modifier = navHostModifier,
		)
	}
}
