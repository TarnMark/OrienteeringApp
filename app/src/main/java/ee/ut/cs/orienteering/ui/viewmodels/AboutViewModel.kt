package ee.ut.cs.orienteering.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class AboutViewModel : ViewModel() {

    fun onBackClicked(navController: NavController) {
        navController.navigate("home")
    }
}