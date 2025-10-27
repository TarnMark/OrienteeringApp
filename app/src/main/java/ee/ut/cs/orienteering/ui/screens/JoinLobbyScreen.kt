package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.ui.viewmodels.JoinLobbyViewModel

@Composable
fun JoinLobbyScreen(
    navController: NavController,
    viewModel: JoinLobbyViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter lobby code", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Lobby code") },
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.joinLobby(code) { questId ->
                    navController.navigate("map/$questId")
                }
            },
            enabled = !state.isLoading && code.isNotBlank()
        ) {
            Text(if (state.isLoading) "Joining..." else "Join")
        }

        state.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
