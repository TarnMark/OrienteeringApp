package ee.ut.cs.orienteering.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Question)

    @Update
    suspend fun update(question: Question)

    @Query("SELECT * FROM questions_table WHERE id = :id")
    fun getQuestionById(id: Int): Flow<Question?>

    @Query("SELECT * FROM questions_table WHERE questId = :questId ORDER BY id ASC")
    fun getQuestionsForQuest(questId: Int): Flow<List<Question>>

    @Query("DELETE FROM questions_table")
    suspend fun deleteAll()
}