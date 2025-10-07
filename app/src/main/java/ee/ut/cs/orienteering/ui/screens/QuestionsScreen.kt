package ee.ut.cs.orienteering.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ee.ut.cs.orienteering.ui.viewmodels.QuestionsViewModel

@Composable
fun QuestionsScreen(vm: QuestionsViewModel = viewModel()) {
    val questions by vm.questions.collectAsState()

    var index by rememberSaveable { mutableStateOf(0) }
    var userAnswer by rememberSaveable { mutableStateOf("") }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions yet", fontSize = 20.sp)
        }
        return
    }

    if (index !in questions.indices) index = 0
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
        Text(q.questionText, fontSize = 20.sp)

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it },
            label = { Text("Type your answer") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            userAnswer = ""
            index = if (index < questions.lastIndex) index + 1 else 0
        }) {
            Text("Next")
        }
    }
}
