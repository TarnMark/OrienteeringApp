package ee.ut.cs.orienteering.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.ut.cs.orienteering.data.Question

@Composable
fun QuestionRow(
    question: Question,
    modifier: Modifier = Modifier,
    onCheckChanged: (Boolean) -> Unit
) {
    //TODO question checking (✅) functionality
}
