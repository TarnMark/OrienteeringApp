package ee.ut.cs.orienteering.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_table")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val questionText: String,
    val answer: String,
    val isAnswered: Boolean = false
)