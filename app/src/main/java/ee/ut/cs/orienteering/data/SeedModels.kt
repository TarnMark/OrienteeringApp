package ee.ut.cs.orienteering.data

data class QuestionDto(
    val id: String,
    val text: String,
    val type: String,
    val options: List<String> = emptyList(),
    val correctIndex: Int? = null
)

data class CheckpointDto(
    val id: String,
    val lat: Double,
    val lon: Double,
    val radiusM: Int,
    val questionId: String
)
