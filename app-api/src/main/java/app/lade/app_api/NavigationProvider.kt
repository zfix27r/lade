package app.lade.app_api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface NavigationProvider {
    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    )
}