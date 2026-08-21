package app.lade.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.lade.R
import app.lade.calendar.ui.CalendarScreen
import app.lade.categories.ui.edit.CategoryEditScreen
import app.lade.categories.ui.list.CategoriesListScreen
import app.lade.habits.ui.edit.HabitEditScreen
import app.lade.habits.ui.list.HabitsListScreen
import app.lade.chat.ui.ChatScreen
import app.lade.time.ui.edit.TimeBlockEditScreen
import app.lade.time.ui.edit.TimeScheduleEditScreen
import app.lade.time.ui.list.TimeSchedulesListScreen
import app.lade.ui.screens.CalendarsStubScreen
import app.lade.ui.screens.DevicesStubScreen
import app.lade.ui.screens.MoreScreen

private data class BottomTab(
	val route: String,
	val labelRes: Int,
	val icon: ImageVector,
)

@Composable
fun LadeApp() {
	val navController = rememberNavController()
	val backStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute = backStackEntry?.destination?.route
	val showBottomBar = currentRoute in Routes.bottomBarRoutes

	val tabs = listOf(
		BottomTab(Routes.Calendar, R.string.nav_calendar, Icons.Default.CalendarMonth),
		BottomTab(Routes.Chat, R.string.nav_chat, Icons.Default.Chat),
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

	Scaffold(
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
		NavHost(
			navController = navController,
			startDestination = Routes.Calendar,
			modifier = Modifier.padding(padding),
		) {
			composable(Routes.Calendar) {
				CalendarScreen(
					onAddBlock = { dateEpochDay ->
						navController.navigate(Routes.timeBlockEdit(dateEpochDay = dateEpochDay))
					},
					onEditBlock = { blockId, dateEpochDay ->
						navController.navigate(Routes.timeBlockEdit(blockId, dateEpochDay))
					},
					onAddHabit = { navController.navigate(Routes.habitEdit()) },
				)
			}
			composable(Routes.Chat) {
				ChatScreen()
			}
			composable(Routes.Habits) {
				HabitsListScreen(
					onBack = { navController.popBackStack() },
					onAdd = { navController.navigate(Routes.habitEdit()) },
					onEdit = { id -> navController.navigate(Routes.habitEdit(id)) },
				)
			}
			composable(Routes.TimeSchedules) {
				TimeSchedulesListScreen(
					onBack = { navController.popBackStack() },
					onAdd = { navController.navigate(Routes.timeScheduleEdit()) },
					onEdit = { id -> navController.navigate(Routes.timeScheduleEdit(id)) },
				)
			}
			composable(Routes.More) {
				MoreScreen(
					onOpenCalendar = { switchTab(Routes.Calendar) },
					onHabits = { navController.navigate(Routes.Habits) },
					onTime = { navController.navigate(Routes.TimeSchedules) },
					onCategories = { navController.navigate(Routes.Categories) },
					onDevices = { navController.navigate(Routes.Devices) },
					onCalendars = { navController.navigate(Routes.Calsync) },
				)
			}
			composable(Routes.Devices) {
				DevicesStubScreen(onBack = { navController.popBackStack() })
			}
			composable(Routes.Calsync) {
				CalendarsStubScreen(onBack = { navController.popBackStack() })
			}
			composable(Routes.Categories) {
				CategoriesListScreen(
					onBack = { navController.popBackStack() },
					onAdd = { navController.navigate(Routes.categoryEdit()) },
					onEdit = { id -> navController.navigate(Routes.categoryEdit(id)) },
				)
			}
			composable(
				route = Routes.CategoryEdit,
				arguments = listOf(navArgument("categoryId") { type = NavType.LongType }),
			) {
				CategoryEditScreen(onBack = { navController.popBackStack() })
			}
			composable(
				route = Routes.HabitEdit,
				arguments = listOf(navArgument("habitId") { type = NavType.LongType }),
			) {
				HabitEditScreen(onBack = { navController.popBackStack() })
			}
			composable(
				route = Routes.TimeScheduleEdit,
				arguments = listOf(navArgument("scheduleId") { type = NavType.LongType }),
			) {
				TimeScheduleEditScreen(onBack = { navController.popBackStack() })
			}
			composable(
				route = Routes.TimeBlockEdit,
				arguments = listOf(
					navArgument("blockId") { type = NavType.LongType },
					navArgument("dateEpochDay") { type = NavType.LongType },
				),
			) {
				TimeBlockEditScreen(onBack = { navController.popBackStack() })
			}
		}
	}
}
