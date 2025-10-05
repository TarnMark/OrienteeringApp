package ee.ut.cs.orienteering.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.ui.viewmodels.MapViewModel

@Composable
fun MapScreen (
    navController: NavController,
    viewModel: MapViewModel = viewModel()
) {
    Text("Map Screen")
}