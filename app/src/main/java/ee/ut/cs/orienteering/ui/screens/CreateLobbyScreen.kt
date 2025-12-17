package ee.ut.cs.orienteering.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.ui.viewmodels.CreateLobbyViewModel

/**
 * Screen for creating a new lobby (quest) within the application.
 *
 * UI structure:
 * - Top app bar with a back button
 * - Text fields for lobby title and optional lobby code
 * - Button to create the lobby
 *
 * Behavior:
 * - The user must enter a non‑blank lobby title to enable the Create button.
 * - When the Create button is pressed:
 *   - A loading state is shown
 *   - [CreateLobbyViewModel.createLobby] is invoked
 *   - On success, navigation proceeds to the map editor screen for the new quest
 * - The system back button and the top bar back button both navigate back.
 *
 * @param navController The [NavController] used to navigate after lobby creation.
 * @param viewModel The [CreateLobbyViewModel] responsible for creating the lobby.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLobbyScreen(
    navController: NavController,
    viewModel: CreateLobbyViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    // Handle system back button
    BackHandler { navController.popBackStack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Lobby", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_leave),
                            tint = colors.onPrimary
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
        val columnPadding = dimensionResource(id = R.dimen.column_padding)
        val rowSpacing = dimensionResource(id = R.dimen.column_row_spacing)


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(columnPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.lobby_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = if (isSystemInDarkTheme()) {
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    OutlinedTextFieldDefaults.colors()
                }
            )

            Spacer(modifier = Modifier.height(rowSpacing))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Lobby code (optional)") },
                singleLine = true,
                supportingText = { Text(stringResource(R.string.no_lobby_code)) },
                modifier = Modifier.fillMaxWidth(),
                colors = if (isSystemInDarkTheme()) {
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    OutlinedTextFieldDefaults.colors()
                }
            )

            Button(
                onClick = {
                    isCreating = true
                    viewModel.createLobby(title = title, codeInput = code) { newQuestId ->
                        isCreating = false
                        navController.navigate("map/true/$newQuestId")
                    }
                },
                enabled = title.isNotBlank() && !isCreating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isCreating) stringResource(R.string.btn_create_lobby_loading) else stringResource(R.string.btn_create_lobby))
            }

        }
    }
}
