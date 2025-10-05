package ee.ut.cs.orienteering.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.ui.viewmodels.QuestionsViewModel

@Composable
fun QuestionsScreen (
    navController: NavController,
    viewModel: QuestionsViewModel = viewModel()
) {
    Text("Questions Screen")
}