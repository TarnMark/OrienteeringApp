package ee.ut.cs.orienteering.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A data class to hold the relationship between a Quest and its list of Questions.
 */
data class QuestWithQuestions(
    @Embedded val quest: Quest,
    @Relation(
        parentColumn = "id",
        entityColumn = "questId"
    )
    val questions: List<Question>
)