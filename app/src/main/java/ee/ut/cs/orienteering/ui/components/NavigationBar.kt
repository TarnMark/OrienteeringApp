package ee.ut.cs.orienteering.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ee.ut.cs.orienteering.ui.navigation.NavItem

@Composable
fun NavigationBar(navController: NavHostController) {
    val items = listOf(
        NavItem("home", "Home", Icons.Default.Home),
        NavItem("map", "Map", Icons.Default.Map),
        // label оставляем "Questions"
        NavItem("questions", "Questions", Icons.AutoMirrored.Filled.List)
    )

    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination
        val currentRoute = currentDestination?.route ?: ""
        val currentQuestId = backStackEntry?.arguments?.getString("questId")

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = when (item.route) {
                    "questions" -> currentRoute.startsWith("questions")
                    "map" -> currentRoute.startsWith("map")
                    else -> currentDestination.isTopLevelDestinationInHierarchy(item.route)
                },
                onClick = {
                    when (item.route) {
                        "questions" -> {
                            if (currentQuestId != null) {
                                navController.navigate("questions/$currentQuestId") {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate("join") {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        }
                        else -> {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
