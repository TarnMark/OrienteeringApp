package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.ui.viewmodels.QuestionsViewModel

@Composable
fun QuestionsScreen(vm: QuestionsViewModel = viewModel()) {
    val questions by vm.questions.collectAsState()
    val buttonSpacing = dimensionResource(id = R.dimen.button_spacing)
    val questionTextSize = 24.sp

    var index by rememberSaveable { mutableStateOf(0) }
    var userAnswer by rememberSaveable { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions yet", fontSize = questionTextSize)
        }
        return
    }

    if (index !in questions.indices) index = 0
    val q = questions[index]
    Box (
        Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Question ${index + 1}/${questions.size}", fontSize = 18.sp)

            Spacer(Modifier.height(8.dp))

            Text(q.questionText, fontSize = questionTextSize)

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = userAnswer,
                onValueChange = {
                    userAnswer = it
                    if (showError && it.isNotBlank()) showError = false },
                label = { Text("Type your answer") },
                singleLine = true,
                isError = showError,
                modifier = Modifier.fillMaxWidth()
            )
            if (showError) {
                Text(
                    text = "Question cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }

            Spacer(Modifier.height(buttonSpacing))
            Button(onClick = {
                if (userAnswer.isBlank()) {
                    showError = true
                } else {
                    userAnswer = ""
                    index = if (index < questions.lastIndex) index + 1 else 0
                }
            }) {
                Text("Next")
            }
        }
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 100.dp).padding(20.dp),
            onClick = { showDialog = true },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Question")
        }

        if (showDialog) {
            AddQuestionDialog(
                onDismiss = { showDialog = false },
                onConfirm = { text ->
                    vm.addQuestion(text)
                    showDialog = false
                }
            )
        }
    }
}
    @Composable
    fun AddQuestionDialog(
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit
    ) {
        var questionText by remember { mutableStateOf("") }
        var latitudeText by remember { mutableStateOf("") }
        var longitudeText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Question") },
            text = {
                Column {
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text("Question text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(questionText)
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }
