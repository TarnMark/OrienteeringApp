package ee.ut.cs.orienteering.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The main Room database for the application.
 *
 * This database stores two entity types:
 * - [Question] — individual questions belonging to quests
 * - [Quest] — lobby/quest metadata
 *
 * Responsibilities:
 * - Provide DAO accessors for [QuestionDao] and [QuestDao]
 * - Expose a thread‑safe singleton instance via [getDatabase]
 *
 * The database is built using the name `"orienteering_database"` and does not
 * export a schema.
 */
@Database(entities = [Question::class, Quest::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    /** DAO for accessing and modifying question data. */
    abstract fun questionDao(): QuestionDao
    /** DAO for accessing and modifying quest data. */
    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of [AppDatabase].
         *
         * Ensures that only one database instance is created across the entire app
         * by synchronizing initialization and storing it in [INSTANCE].
         *
         * @param context The application context used to build the database.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "orienteering_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}