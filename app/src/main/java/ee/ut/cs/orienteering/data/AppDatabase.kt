package ee.ut.cs.orienteering.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Question::class, Quest::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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