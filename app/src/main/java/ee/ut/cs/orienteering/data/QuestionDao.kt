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

    @Query("SELECT * FROM question_table WHERE id = :id")
    fun getQuestionById(id: Int): Flow<Question?>

    @Query("SELECT * FROM question_table ORDER BY questionText ASC")
    fun getAllQuestions(): Flow<List<Question>>

    @Query("DELETE FROM question_table")
    suspend fun deleteAll()
}