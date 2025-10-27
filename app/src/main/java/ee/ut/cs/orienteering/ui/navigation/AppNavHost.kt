package ee.ut.cs.orienteering.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ee.ut.cs.orienteering.ui.screens.AboutScreen
import ee.ut.cs.orienteering.ui.screens.CreateLobbyScreen
import ee.ut.cs.orienteering.ui.screens.HomeScreen
import ee.ut.cs.orienteering.ui.screens.JoinLobbyScreen
import ee.ut.cs.orienteering.ui.screens.MapScreen
import ee.ut.cs.orienteering.ui.screens.QuestionsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("about") { AboutScreen(navController) }
        composable("join_lobby") { JoinLobbyScreen(navController) }
        composable("create_lobby") { CreateLobbyScreen(navController) }

        composable("map/{questId}") { backStackEntry ->
            val questId = backStackEntry.arguments?.getString("questId")?.toInt() ?: 0
            MapScreen(navController = navController, questId = questId)
        }


        composable("questions/{questId}") { backStackEntry ->
            val questId = backStackEntry.arguments?.getString("questId")?.toInt() ?: 0
            QuestionsScreen(questId = questId)
        }
    }}
