package ee.ut.cs.orienteering.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing CRUD operations on [Quest] entities.
 *
 * Responsibilities:
 * - Insert, update, and delete quests
 * - Query quests by ID or join code
 * - Retrieve quests together with their related questions
 * - Provide reactive streams via [Flow] for observing database changes
 *
 * This DAO operates on the `quests_table` defined in [Quest].
 */
@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quest: Quest): Long
    @Update
    suspend fun update(quest: Quest)

    @Delete
    suspend fun delete(quest: Quest)
    @Query("DELETE FROM quests_table")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM quests_table")
    suspend fun count(): Int

    @Query("SELECT * FROM quests_table WHERE id = :id")
    fun getQuestById(id: Int): Flow<Quest?>

    @Transaction
    @Query("SELECT * FROM quests_table WHERE id = :questId")
    fun getQuestWithQuestions(questId: Int): Flow<QuestWithQuestions?>
    @Query("SELECT * FROM quests_table WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): Quest?

}