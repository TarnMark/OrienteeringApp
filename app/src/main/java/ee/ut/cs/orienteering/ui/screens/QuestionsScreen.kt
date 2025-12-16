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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.ui.components.QuestionRow
import ee.ut.cs.orienteering.ui.viewmodels.QuestionsViewModel

/**
 * Displays the list of questions for a specific quest and allows adding new questions.
 *
 * UI structure:
 * - A centered top app bar showing the quest ID
 * - A floating action button for adding a new question
 * - A scrollable list of existing questions
 *
 * Behavior:
 * - Observes questions, checked states, and answers from [QuestionsViewModel].
 * - Each question is displayed using [QuestionRow], allowing:
 *   - Toggling the checked state
 *   - Editing the answer text
 * - When the FAB is pressed, an [AddQuestionDialog] is shown.
 * - Submitting the dialog adds a new question to the quest.
 *
 * @param questId The ID of the quest whose questions should be displayed.
 * @param viewModel The [QuestionsViewModel] providing question data and update logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(questId: Int, viewModel: QuestionsViewModel = viewModel()) {
    val questions by viewModel.questionsForQuest(questId).collectAsState(initial = emptyList())
    val checked by viewModel.checked.collectAsState()
    val answers by viewModel.answers.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    val buttonPadding = dimensionResource(id = R.dimen.floating_act_button_padding)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.lobby_questions_title, questId)) }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                modifier = Modifier.padding(bottom = buttonPadding)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.btn_add_question))
            }
        }
    ) { innerPadding ->
        val screenPadding = dimensionResource(id = R.dimen.screen_padding)
        val rowPadding = dimensionResource(id = R.dimen.text_row_padding)

        Column(Modifier.padding(innerPadding).padding(screenPadding)) {
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
                            modifier = Modifier.padding(vertical = rowPadding)
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


/**
 * Dialog for adding a new question to a quest.
 *
 * Behavior:
 * - Allows the user to enter question text.
 * - The confirm button is enabled only when the text is not blank.
 * - On confirmation, calls [onConfirm] with the entered text.
 * - On dismissal, calls [onDismiss].
 *
 * @param onDismiss Callback invoked when the dialog is dismissed.
 * @param onConfirm Callback invoked with the new question text when the user confirms.
 */
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
