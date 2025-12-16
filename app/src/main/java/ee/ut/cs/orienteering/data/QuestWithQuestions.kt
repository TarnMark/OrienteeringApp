package ee.ut.cs.orienteering.data

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.Serializable

/**
 * Combined data model representing a [Quest] and all of its associated [Question]s.
 *
 * This class is used by Room to load relational data using:
 * - [@Embedded] to include the parent [Quest] fields directly
 * - [@Relation] to fetch all [Question] entities whose `questId` matches the quest's `id`
 *
 * It is annotated with `@Serializable` so it can be included in import/export
 * operations (e.g., JSON bundles or QR‑based transfers).
 */
@Serializable
data class QuestWithQuestions(
    @Embedded val quest: Quest,
    @Relation(
        parentColumn = "id",
        entityColumn = "questId"
    )
    val questions: List<Question>
)