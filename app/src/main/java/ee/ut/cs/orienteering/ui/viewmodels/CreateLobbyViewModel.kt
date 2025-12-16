package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.Quest
import ee.ut.cs.orienteering.data.QuestDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel responsible for creating new lobbies (quests) and generating unique lobby codes.
 *
 * Responsibilities:
 * - Insert new [Quest] entries into the database
 * - Ensure that each lobby code is unique
 * - Generate random fallback codes when the user does not provide one
 *
 * This ViewModel can be constructed either with an [Application] (default)
 * or with a custom [QuestDao] for testing.
 */
open class CreateLobbyViewModel : AndroidViewModel {

    private val questDao: QuestDao

    /**
     * Default constructor using the application's database instance.
     *
     * @param application The application context used to access the Room database.
     */
    constructor(application: Application) : super(application) {
        questDao = AppDatabase.getDatabase(application).questDao()
    }

    /**
     * Secondary constructor allowing injection of a custom [QuestDao].
     * Useful for unit testing.
     *
     * @param application The application context.
     * @param questDao A DAO instance supplied externally.
     */
    constructor(application: Application, questDao: QuestDao) : super(application) {
        this.questDao = questDao
    }

    /**
     * Creates a new lobby (quest) with the given title and optional code.
     *
     * Workflow:
     * - Ensures the lobby code is unique (generates one if needed)
     * - Inserts the new quest into the database
     * - Invokes [onCreated] with the new quest ID
     *
     * @param title The lobby/quest title.
     * @param codeInput Optional user‑provided lobby code.
     * @param onCreated Callback invoked with the newly created quest ID.
     */
    open fun createLobby(title: String, codeInput: String?, onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val finalCode = withContext(Dispatchers.IO) { ensureUniqueCode(codeInput) }
            val id = withContext(Dispatchers.IO) {
                questDao.insert(Quest(0, title, finalCode))
            }
            onCreated(id.toInt())
        }
    }

    /**
     * Ensures that the lobby code is unique.
     *
     * Behavior:
     * - Uses the user‑provided code if valid
     * - Generates a random code if none is provided
     * - Repeats generation until a unique code is found
     *
     * @param userInput The optional code entered by the user.
     * @return A unique lobby code.
     */
    suspend fun ensureUniqueCode(userInput: String?): String {
        var candidate = userInput?.trim().orEmpty()
        if (candidate.isBlank()) candidate = generateLobbyCode()
        while (questDao.getByCode(candidate) != null) {
            candidate = generateLobbyCode()
        }
        return candidate
    }

    /**
     * Generates a random 5‑character lobby code consisting of A–Z and 0–9.
     *
     * @return A random alphanumeric code.
     */
    private fun generateLobbyCode(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..5).joinToString("") { chars.random().toString() }
    }
}


