package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ee.ut.cs.orienteering.ui.components.QuestionRow
import ee.ut.cs.orienteering.ui.viewmodels.QuestionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(questId: Int, viewModel: QuestionsViewModel = viewModel()) {
    val questions by viewModel.questionsForQuest(questId).collectAsState(initial = emptyList())
    val checked by viewModel.checked.collectAsState()
    val answers by viewModel.answers.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Lobby #$questId Questions") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add question")
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).padding(12.dp)) {
            if (questions.isEmpty()) {
                Text("No questions yet for this lobby.")
            } else {
                LazyColumn {
                    items(questions, key = { it.id }) { q ->
                        QuestionRow(
                            question = q,
                            isChecked = checked.contains(q.id),
                            answerText = answers[q.id] ?: "",
                            onCheckedToggle = { viewModel.toggleChecked(q.id) },
                            onAnswerChanged = { viewModel.updateAnswer(q.id, it) },
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddQuestionDialog(
            onDismiss = { showAdd = false },
            onConfirm = { text -> viewModel.addQuestion(text, questId).also { showAdd = false } }
        )
    }
}



@Composable
private fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add question") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Question text") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) {
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
