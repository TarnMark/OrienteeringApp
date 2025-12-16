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

/**
 * Application-wide navigation host defining all composable destinations.
 *
 * This function sets up the navigation graph for the app, including:
 * - Static screens (home, about, join lobby, create lobby)
 * - Parameterized routes (map, questions)
 *
 * Route formats:
 * - `"map/{editable}/{questId}"`
 *   - `editable`: `"true"` or `"false"`
 *   - `questId`: Integer quest identifier
 *
 * - `"questions/{questId}"`
 *   - `questId`: Integer quest identifier
 *
 * @param navController The [NavHostController] used to navigate between screens.
 * @param modifier Optional [Modifier] for layout customization.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("about") { AboutScreen(navController) }
        composable("join_lobby") { JoinLobbyScreen(navController) }
        composable("create_lobby") { CreateLobbyScreen(navController) }

        composable("map/{editable}/{questId}") { backStackEntry ->
            val questId = backStackEntry.arguments?.getString("questId")?.toInt() ?: 0
            val editable = backStackEntry.arguments?.getString("editable").toBoolean()
            MapScreen(navController = navController, questId = questId, editable = editable)
        }


        composable("questions/{questId}") { backStackEntry ->
            val questId = backStackEntry.arguments?.getString("questId")?.toInt() ?: 0
            QuestionsScreen(questId = questId)
        }
    }
}
