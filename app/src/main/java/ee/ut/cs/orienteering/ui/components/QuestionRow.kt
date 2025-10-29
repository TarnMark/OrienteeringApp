package ee.ut.cs.orienteering.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.data.Question

@Composable
fun QuestionRow(
    question: Question,
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    answerText: String,
    onCheckedToggle: () -> Unit,
    onAnswerChanged: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme

    val backgroundColor = when {
        isChecked -> colors.secondaryContainer.copy(alpha = 0.5f)
        isExpanded -> colors.surfaceVariant
        else -> colors.surface
    }
    val animatedBackground by animateColorAsState(targetValue = backgroundColor)


    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .animateContentSize()
            .clickable { isExpanded = !isExpanded }
            .background(animatedBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question.questionText,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (isChecked)
                    colors.onSurface.copy(alpha = 0.6f)
                else
                    colors.onSurface
            )

            Checkbox(
                checked = isChecked,
                onCheckedChange = { onCheckedToggle() }
            )
        }

        // Expanded answer input field
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = answerText,
                onValueChange =  onAnswerChanged ,
                label = { Text(stringResource(R.string.answer_field)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
