package ee.ut.cs.orienteering.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions_table",
    foreignKeys = [
        ForeignKey(
            entity = Quest::class,
            parentColumns = ["id"],
            childColumns = ["questId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["questId"])]
)
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val questId: Int,
    val questionText: String,
    val answer: String,
    val location: String
)