package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ee.ut.cs.orienteering.data.SeedLoader

@Composable
fun QuestionsScreen() {
    val context = LocalContext.current
    val questions = remember { SeedLoader.loadQuestions(context) }

    if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No data", fontSize = 20.sp)
        }
    } else {
        var index by remember { mutableStateOf(0) }
        var userAnswer by remember { mutableStateOf("") }
        var selectedOption by remember { mutableStateOf<Int?>(null) }
        val q = questions[index]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Question ${index + 1}/${questions.size}", fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text(q.text, fontSize = 20.sp)
            Spacer(Modifier.height(24.dp))

            if (q.options.isNotEmpty()) {
                q.options.forEachIndexed { i, option ->
                    val isSelected = selectedOption == i
                    Button(
                        onClick = { selectedOption = i },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isSelected)
                                MaterialTheme.colorScheme.onTertiary
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(option)
                    }
                }
            } else {
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Type your answer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    selectedOption = null
                    userAnswer = ""
                    if (index < questions.lastIndex) index++ else index = 0
                }
            ) { Text("Next question") }
        }
    }
}