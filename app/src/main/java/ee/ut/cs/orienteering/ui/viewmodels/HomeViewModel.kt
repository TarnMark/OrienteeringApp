package ee.ut.cs.orienteering.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class HomeViewModel : ViewModel() {
    fun onCreateLobbyClicked(navController: NavController) {
        navController.navigate("create_lobby")
    }

    fun onJoinLobbyClicked(navController: NavController) {
        navController.navigate("join_lobby")
    }

    fun onAboutClicked(navController: NavController) {
        navController.navigate("about")
    }
}
