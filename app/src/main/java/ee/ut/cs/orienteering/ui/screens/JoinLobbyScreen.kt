package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
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
import ee.ut.cs.orienteering.ui.viewmodels.JoinLobbyViewModel

/**
 * Screen for joining an existing lobby by entering a lobby code.
 *
 * UI structure:
 * - Top app bar with a back button
 * - Text field for entering a lobby code
 * - Join button that triggers the join flow
 * - Optional error message displayed below the button
 *
 * Behavior:
 * - The Join button is enabled only when:
 *   - The lobby code is not blank
 *   - The view model is not currently loading
 * - When the Join button is pressed:
 *   - [JoinLobbyViewModel.joinLobby] is invoked
 *   - On success, navigation proceeds to the map screen in non‑editable mode
 * - If an error occurs, the error message from the state is displayed.
 *
 * @param navController The [NavController] used to navigate after a successful join.
 * @param viewModel The [JoinLobbyViewModel] providing state and join logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinLobbyScreen(
    navController: NavController,
    viewModel: JoinLobbyViewModel = viewModel()
) {
    val buttonSpacing = dimensionResource(id = R.dimen.button_spacing)
    val textTitleSpacing = dimensionResource(id = R.dimen.text_field_title_spacing)
    val errorHeight = dimensionResource(id = R.dimen.error_message_height)

    val state by viewModel.state.collectAsState()
    var code by remember { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme

    Scaffold (topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.btn_back),
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
    })
    {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.enter_lobby_code), style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(textTitleSpacing))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(stringResource(R.string.lobby_code)) },
                singleLine = true,
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

            Spacer(Modifier.height(buttonSpacing))

            Button(
                onClick = {
                    viewModel.joinLobby(code) { questId ->
                        navController.navigate("map/false/$questId")
                    }
                },
                enabled = !state.isLoading && code.isNotBlank()
            ) {
                Text(if (state.isLoading) stringResource(R.string.btn_join_loading) else stringResource(R.string.btn_join))
            }

            state.errorMessage?.let {
                Spacer(Modifier.height(errorHeight))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

        }
    }
}
