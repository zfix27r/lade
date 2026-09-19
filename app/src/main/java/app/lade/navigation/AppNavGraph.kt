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
import app.lade.chat.ui.ChatScreen
import app.lade.chat.ui.templates.ChatDictEditScreen
import app.lade.chat.ui.templates.ChatTemplatesListScreen
import app.lade.draft.DraftApi
import app.lade.draft.DraftEditorScreen
import app.lade.entrydetailsscreen.ui.list.EntryListScreen
import app.lade.more.ui.MoreScreen
import app.lade.reminders.ui.DayPartSettingsScreen
import app.lade.synccalendar.ui.CalendarsStubScreen
import app.lade.syncdevices.ui.DevicesStubScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
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
                onEntryClick = { entryId ->
                    TODO()
                },
                onOpenProfile = onOpenProfile,
            )
        }

        composable(Routes.Chat) {
            ChatScreen(
                onOpenProfile = onOpenProfile,
                onOpenTemplates = { navController.navigate(Routes.ChatDicts) },
                onCreateEntry = { _, title ->
                    // TODO: ChatScreen тоже создаёт запись через draftApi
                    // пока — заглушка
                },
            )
        }

        composable(Routes.ChatDicts) {
            ChatTemplatesListScreen(
                onBack = { navController.popBackStack() },
                onAddDict = { navController.navigate(Routes.chatDictEdit()) },
                onEditDict = { id -> navController.navigate(Routes.chatDictEdit(id)) },
            )
        }

        composable(
            route = Routes.ChatDictEdit,
            arguments = listOf(navArgument("dictId") { type = NavType.LongType }),
        ) {
            ChatDictEditScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.Entries) {
            EntryListScreen(
                onAdd = { TODO() },
                onEdit = { id, _ -> TODO()   },
            )
        }

        composable(Routes.Settings) {
            MoreScreen(
                onOpenProfile = onOpenProfile,
                onEntries = { navController.navigate(Routes.Entries) },
                onCategories = { navController.navigate(Routes.Categories) },
                onDevices = { navController.navigate(Routes.Devices) },
                onCalendars = { navController.navigate(Routes.Calsync) },
                onDayPartSettings = { navController.navigate(Routes.DayPartSettings) },
                onKindPrioritySettings = { navController.navigate(Routes.KindPrioritySettings) },
                onOpenCalendar = TODO(),
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

        composable(Routes.DraftEditor) {
            DraftEditorScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}