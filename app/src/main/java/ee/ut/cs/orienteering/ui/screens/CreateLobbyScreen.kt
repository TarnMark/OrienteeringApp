package ee.ut.cs.orienteering.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ee.ut.cs.orienteering.ui.viewmodels.CreateLobbyViewModel
import androidx.compose.runtime.remember


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLobbyScreen(
    navController: NavController,
    viewModel: CreateLobbyViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

    BackHandler { navController.popBackStack() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Lobby") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Lobby name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Lobby code (optional)") },
                singleLine = true,
                supportingText = { Text("If no code, will be generated automatically") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    isCreating = true
                    viewModel.createLobby(title = title, codeInput = code) { newQuestId ->
                        isCreating = false
                        navController.navigate("map/$newQuestId")
                    }
                },
                enabled = title.isNotBlank() && !isCreating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isCreating) "Creating..." else "Create Lobby")
            }

        }
    }
}
