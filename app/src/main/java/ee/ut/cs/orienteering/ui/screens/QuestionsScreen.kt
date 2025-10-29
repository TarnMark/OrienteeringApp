package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ee.ut.cs.orienteering.R
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
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.lobby_questions_title, questId)) }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_add_question))
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).padding(12.dp)) {
            if (questions.isEmpty()) {
                Text(stringResource(R.string.no_questions))
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
        title = { Text(stringResource(R.string.btn_add_question)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.question_text)) }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) { Text(stringResource(R.string.btn_add)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) }
        }
    )
}
