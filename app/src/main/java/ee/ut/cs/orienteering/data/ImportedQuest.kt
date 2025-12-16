package ee.ut.cs.orienteering.data

import kotlinx.serialization.Serializable

/**
 * Data model used for importing or exporting a full quest bundle.
 *
 * This structure represents a complete quest package containing:
 * - [quest] — the quest metadata (title, code, etc.)
 * - [questions] — all questions associated with the quest
 *
 * It is annotated with `@Serializable` so it can be encoded/decoded
 * using Kotlin Serialization when transferring quests via JSON or QR codes.
 */
@Serializable
data class ImportedQuest(
    val quest: Quest,
    val questions: List<Question>
)
