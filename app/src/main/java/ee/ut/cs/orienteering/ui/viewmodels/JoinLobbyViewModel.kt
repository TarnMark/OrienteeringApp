package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.ImportedQuest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * UI state for the lobby‑joining flow.
 *
 * @property isLoading Indicates whether a join request is currently in progress.
 * @property errorMessage Optional error message shown when joining fails.
 *
 * Used by [JoinLobbyViewModel] to expose loading and error states to the UI.
 */
data class LobbyUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel responsible for handling the logic of joining an existing lobby
 * and importing quests from external JSON data.
 *
 * Responsibilities:
 * - Validate and look up lobbies by their join code
 * - Expose loading and error state via [state]
 * - Import quests and their questions from a JSON payload
 *
 * This ViewModel uses DAOs from the Room database to read and update quest data.
 *
 * @param app The application context used to access the Room database.
 */
open class JoinLobbyViewModel(app: Application) : AndroidViewModel(app) {

    private val questDao = AppDatabase.getDatabase(app).questDao()
    private val questionDao = AppDatabase.getDatabase(app).questionDao()

    private val _state = MutableStateFlow(LobbyUiState())

    /**
     * Public read‑only state representing the current lobby join UI state.
     */
    open val state: StateFlow<LobbyUiState> = _state

    /**
     * Attempts to join a lobby using the provided [code].
     *
     * Workflow:
     * - Sets loading state
     * - Looks up the quest by its code
     * - If found, invokes [onSuccess] with the quest ID
     * - If not found, updates state with an error message
     * - Catches unexpected errors and exposes them to the UI
     *
     * @param code The lobby code entered by the user.
     * @param onSuccess Callback invoked with the quest ID when the lobby is found.
     */
    open fun joinLobby(code: String, onSuccess: (questId: Int) -> Unit) = viewModelScope.launch {
        _state.value = LobbyUiState(isLoading = true)
        try {
            val quest = questDao.getByCode(code.trim())
            if (quest != null) {
                onSuccess(quest.id)
                _state.value = LobbyUiState()
            } else {
                _state.value = LobbyUiState(errorMessage = "Invalid lobby code")
            }
        } catch (t: Throwable) {
            _state.value = LobbyUiState(errorMessage = "Error: ${t.message}")
        }
    }

    /**
     * Imports a quest and its questions from a JSON string.
     *
     * Behavior:
     * - Decodes the JSON into an [ImportedQuest] structure
     * - Inserts or updates the quest based on its code
     * - Deletes existing questions for that quest
     * - Inserts the new set of questions
     *
     * @param json The JSON string representing a quest and its questions.
     * @return The lobby code associated with the imported quest.
     */
    suspend fun importQuestFromJson(json: String): String {
        val data = Json.decodeFromString<ImportedQuest>(json)
        val code = data.quest.code
        val quest = data.quest.copy(id = 0)

        // Save or update quest
        if (questDao.getByCode(code)==null)
                questDao.insert(quest).toInt()
        else questDao.update(quest)

        // Replace questions for this quest
        val questId = questDao.getByCode(code)?.id
        questionDao.deleteByQuest(questId!!)
        data.questions.forEach { q ->
            questionDao.insert(q.copy(id = 0, questId = questId))
        }
        return code
    }

}
