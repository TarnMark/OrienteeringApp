package ee.ut.cs.orienteering.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ee.ut.cs.orienteering.ui.screens.HomeScreen
import ee.ut.cs.orienteering.ui.screens.JoinLobbyScreen
import ee.ut.cs.orienteering.ui.screens.CreateLobbyScreen
import ee.ut.cs.orienteering.ui.screens.MapScreen
import ee.ut.cs.orienteering.ui.screens.QuestionsScreen
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController) }
        composable("join_lobby") { JoinLobbyScreen(navController) }
        composable("create_lobby") { CreateLobbyScreen(navController) }
        composable("map") { MapScreen(navController) }
        composable("questions") { QuestionsScreen() }
    }
}
