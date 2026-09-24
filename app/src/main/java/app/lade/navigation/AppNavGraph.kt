package app.lade.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import app.lade.calendar.ui.CalendarScreen
import app.lade.categories.ui.edit.CategoryEditScreen
import app.lade.categories.ui.list.CategoriesListScreen
import app.lade.draft.DraftApi
import app.lade.more.ui.MoreScreen
import app.lade.reminders.ui.DayPartSettingsScreen
import app.lade.synccalendar.ui.CalendarsStubScreen
import app.lade.syncdevices.ui.DevicesStubScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    draftApi: DraftApi,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Calendar,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
        modifier = modifier,
    ) {
        composable(Routes.Calendar) {
            CalendarScreen(
                onOpenProfile = onOpenProfile,
                onOpenEditor = { navController.navigate(Routes.DraftEditor) },
                draftApi = draftApi,
            )
        }

        composable(Routes.Settings) {
            MoreScreen(
                onOpenProfile = onOpenProfile,
                onEntries = { },
                onCategories = { navController.navigate(Routes.Categories) },
                onDevices = { navController.navigate(Routes.Devices) },
                onCalendars = { navController.navigate(Routes.Calsync) },
                onDayPartSettings = { navController.navigate(Routes.DayPartSettings) },
                onKindPrioritySettings = { navController.navigate(Routes.KindPrioritySettings) },
                onOpenCalendar = { navController.navigate(Routes.Calendar) },
                viewModel = TODO(),
            )
        }

        composable(Routes.Devices) {
            DevicesStubScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.Calsync) {
            CalendarsStubScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.DayPartSettings) {
            DayPartSettingsScreen(onBack = { navController.popBackStack() })
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
    }
}