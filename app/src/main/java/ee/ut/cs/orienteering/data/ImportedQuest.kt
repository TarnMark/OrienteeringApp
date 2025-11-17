package ee.ut.cs.orienteering.data

import kotlinx.serialization.Serializable


@Serializable
data class ImportedQuest(
    val quest: Quest,
    val questions: List<Question>
)
