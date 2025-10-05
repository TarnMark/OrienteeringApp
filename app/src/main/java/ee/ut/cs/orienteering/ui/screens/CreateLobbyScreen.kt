package ee.ut.cs.orienteering.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.ui.viewmodels.CreateLobbyViewModel

@Composable
fun CreateLobbyScreen (
    navController: NavController,
    viewModel: CreateLobbyViewModel = viewModel()
) {
    Text("Create Lobby Screen")
}