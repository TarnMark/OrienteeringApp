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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ee.ut.cs.orienteering.R
import ee.ut.cs.orienteering.data.Question

/**
 * Displays a single question row with a checkbox and an expandable answer field.
 *
 * Behavior:
 * - Tapping the row toggles expansion to reveal or hide the answer input field.
 * - Checking the checkbox visually strikes through the question text.
 * - Background color animates based on checked/expanded state.
 *
 * @param question The [Question] model containing the question text and metadata.
 * @param modifier Optional [Modifier] for layout customization.
 * @param isChecked Whether the question is marked as completed.
 * @param answerText The current text entered in the answer field.
 * @param onCheckedToggle Callback invoked when the checkbox is toggled.
 * @param onAnswerChanged Callback invoked when the answer text changes.
 */
@Composable
fun QuestionRow(
    question: Question,
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    answerText: String,
    onCheckedToggle: () -> Unit,
    onAnswerChanged: (String) -> Unit
) {
    // Controls whether the answer field is visible
    var isExpanded by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme

    // Determine background color based on state
    val backgroundColor = when {
        isChecked -> colors.secondaryContainer.copy(alpha = 0.5f)
        isExpanded -> colors.surfaceVariant
        else -> colors.surface
    }
    val animatedBackground by animateColorAsState(targetValue = backgroundColor)

    // Dimension resources
    val textRowPadding = dimensionResource(id = R.dimen.text_row_padding)
    val questionSpacing = dimensionResource(id = R.dimen.text_field_question_spacing)
    val columnPaddingH = dimensionResource(id = R.dimen.column_padding_horizontal)
    val columnPaddingV = dimensionResource(id = R.dimen.column_padding_vertical)


    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .animateContentSize()
            .clickable { isExpanded = !isExpanded }
            .background(animatedBackground)
            .padding(horizontal = columnPaddingH, vertical = columnPaddingV)
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
                    .padding(end = textRowPadding),
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
            Spacer(modifier = Modifier.height(questionSpacing))
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
