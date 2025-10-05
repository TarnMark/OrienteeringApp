package ee.ut.cs.orienteering.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.ui.viewmodels.JoinLobbyViewModel

@Composable
fun JoinLobbyScreen (
    navController: NavController,
    viewModel: JoinLobbyViewModel = viewModel()
) {
    Text("Join Lobby Screen")
}