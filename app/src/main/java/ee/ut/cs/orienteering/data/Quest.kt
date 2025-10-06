package ee.ut.cs.orienteering.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests_table")
data class Quest (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val question: String,
    val location: String,
    val answer: String
)