package ee.ut.cs.orienteering.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

/**
 * ViewModel for the Home screen.
 *
 * Responsibilities:
 * - Handles navigation actions triggered from the Home UI.
 * - Provides simple entry points to the main app flows:
 *   - Creating a lobby
 *   - Joining a lobby
 *   - Viewing the About screen
 */
class HomeViewModel : ViewModel() {

    /**
     * Navigates to the lobby creation screen.
     *
     * @param navController The [NavController] used to perform the navigation.
     */
    fun onCreateLobbyClicked(navController: NavController) {
        navController.navigate("create_lobby")
    }

    /**
     * Navigates to the lobby join screen.
     *
     * @param navController The [NavController] used to perform the navigation.
     */
    fun onJoinLobbyClicked(navController: NavController) {
        navController.navigate("join_lobby")
    }

    /**
     * Navigates to the About screen.
     *
     * @param navController The [NavController] used to perform the navigation.
     */
    fun onAboutClicked(navController: NavController) {
        navController.navigate("about")
    }
}
