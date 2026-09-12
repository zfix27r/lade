package app.lade.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
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
import app.lade.entry.domain.models.EntryKind
import app.lade.entry.ui.edit.EntryEditScreen
import app.lade.entry.ui.list.EntryListScreen
import app.lade.reminders.ui.DayPartSettingsScreen
import app.lade.settings.ui.KindPrioritySettingsScreen
import app.lade.synccalendar.ui.CalendarsStubScreen
import app.lade.syncdevices.ui.DevicesStubScreen
import app.lade.more.ui.MoreScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    bottomNavHeight: Dp,
    switchTab: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Calendar,
        modifier = modifier,
    ) {
        composable(Routes.Calendar) {
            CalendarScreen(
                onCreateEntry = { kind, dateEpochDay ->
                    navController.navigate(Routes.entryEdit(kind = kind, dateEpochDay = dateEpochDay))
                },
                onEditEntry = { entryId ->
                    navController.navigate(Routes.entryEdit(id = entryId))
                },
            )
        }
        composable(Routes.Chat) {
            ChatScreen(
                bottomNavHeight = bottomNavHeight,
                onOpenTemplates = { navController.navigate(Routes.ChatDicts) },
                onCreateEntry = { kind, title ->
                    navController.navigate(
                        Routes.entryEdit(kind = kind, titleHint = title),
                    )
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
                onBack = { navController.popBackStack() },
                onAdd = { kind ->
                    navController.navigate(Routes.entryEdit(kind = kind.storage))
                },
                onEdit = { id, kind ->
                    navController.navigate(Routes.entryEdit(id = id, kind = kind.storage))
                },
            )
        }
        composable(Routes.More) {
            MoreScreen(
                onOpenCalendar = { switchTab(Routes.Calendar) },
                onEntries = { navController.navigate(Routes.Entries) },
                onCategories = { navController.navigate(Routes.Categories) },
                onDevices = { navController.navigate(Routes.Devices) },
                onCalendars = { navController.navigate(Routes.Calsync) },
                onDayPartSettings = { navController.navigate(Routes.DayPartSettings) },
                onKindPrioritySettings = { navController.navigate(Routes.KindPrioritySettings) },
            )
        }
        composable(Routes.KindPrioritySettings) {
            KindPrioritySettingsScreen(onBack = { navController.popBackStack() })
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
        composable(
            route = Routes.EntryEdit,
            arguments = listOf(
                navArgument("entryId") { type = NavType.LongType },
                navArgument("kind") { type = NavType.StringType; defaultValue = EntryKind.TASK.storage },
                navArgument("dateEpochDay") { type = NavType.LongType; defaultValue = -1L },
                navArgument("titleHint") { type = NavType.StringType; defaultValue = "" },
            ),
        ) {
            EntryEditScreen(onBack = { navController.popBackStack() })
        }
    }
}