package ee.ut.cs.orienteering.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

/**
 * ViewModel for the About screen.
 *
 * Responsibilities:
 * - Handles navigation actions originating from the About screen.
 */
class AboutViewModel : ViewModel() {

    /**
     * Navigates back to the home screen.
     *
     * @param navController The [NavController] used to perform the navigation action.
     */
    fun onBackClicked(navController: NavController) {
        navController.navigate("home")
    }
}