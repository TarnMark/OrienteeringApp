package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.ui.viewmodels.HomeViewModel
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.ui.theme.Blue40
import ee.ut.cs.orienteering.ui.theme.Purple40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val screenPadding = dimensionResource(id = R.dimen.screen_padding)
    val buttonHeight = dimensionResource(id = R.dimen.button_height)
    val buttonSpacing = dimensionResource(id = R.dimen.button_spacing)

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Orienteering App", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { viewModel.onAboutClicked(navController) }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
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
}
