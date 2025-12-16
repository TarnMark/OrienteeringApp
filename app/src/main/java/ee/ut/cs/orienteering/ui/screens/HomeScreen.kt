package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.ui.viewmodels.HomeViewModel

/**
 * Displays the main home screen of the application.
 *
 * UI structure:
 * - A top app bar showing the app name and an About button
 * - Two primary actions centered on the screen:
 *   - Create Lobby
 *   - Join Lobby
 *
 * Behavior:
 * - Tapping "Create Lobby" triggers [HomeViewModel.onCreateLobbyClicked] to navigate to the lobby creation flow.
 * - Tapping "Join Lobby" triggers [HomeViewModel.onJoinLobbyClicked] to navigate to the lobby join screen.
 * - Tapping the info icon in the top bar triggers [HomeViewModel.onAboutClicked] to open the About screen.
 *
 * @param navController The [NavController] used to navigate between screens.
 * @param viewModel The [HomeViewModel] handling navigation logic for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme

    val buttonHeight = dimensionResource(id = R.dimen.button_height)
    val buttonSpacing = dimensionResource(id = R.dimen.button_spacing)

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { viewModel.onAboutClicked(navController) }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.about)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary,
                    actionIconContentColor = colors.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),//screenPadding),
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
                Text(text = stringResource(R.string.btn_create_lobby))
            }

            Button(
                onClick = { viewModel.onJoinLobbyClicked(navController) },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(buttonHeight)
            ) {
                Text(text = stringResource(R.string.btn_join_lobby))
            }
        }
    }
    }
}
