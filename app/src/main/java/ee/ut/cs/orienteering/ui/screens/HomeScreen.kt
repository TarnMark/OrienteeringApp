package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.ui.viewmodels.HomeViewModel
import ee.ut.cs.orienteering.R

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val screenPadding = dimensionResource(id = R.dimen.screen_padding)
    val buttonHeight = dimensionResource(id = R.dimen.button_height)
    val buttonSpacing = dimensionResource(id = R.dimen.button_spacing)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(screenPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            Button(
                onClick = { viewModel.onCreateLobbyClicked(navController) },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(buttonHeight)
            ) {
                Text(text = "Create Lobby")
            }

            Button(
                onClick = { viewModel.onJoinLobbyClicked(navController) },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(buttonHeight)
            ) {
                Text(text = "Join Lobby")
            }
        }
    }
}
