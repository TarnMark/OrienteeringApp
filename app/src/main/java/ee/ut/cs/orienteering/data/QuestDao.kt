package ee.ut.cs.orienteering.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quest: Quest)

    @Update
    suspend fun update(quest: Quest)

    @Delete
    suspend fun delete(quest: Quest)

    @Query("SELECT * FROM quests_table WHERE id = :id")
    fun getQuestById(id: Int): Flow<Quest?>

    @Query("SELECT * FROM quests_table ORDER BY question ASC")
    fun getAllQuests(): Flow<List<Quest>>
}