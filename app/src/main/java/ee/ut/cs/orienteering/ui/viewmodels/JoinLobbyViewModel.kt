package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.ImportedQuest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class LobbyUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class JoinLobbyViewModel(app: Application) : AndroidViewModel(app) {

    private val questDao = AppDatabase.getDatabase(app).questDao()
    private val questionDao = AppDatabase.getDatabase(app).questionDao()

    private val _state = MutableStateFlow(LobbyUiState())
    val state: StateFlow<LobbyUiState> = _state

    fun joinLobby(code: String, onSuccess: (questId: Int) -> Unit) = viewModelScope.launch {
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

    // QR code import
    val importedQuestJsonFlow = MutableSharedFlow<String?>(extraBufferCapacity = 1)
    fun emitImportedJson(json: String) {
        Log.d("QR", "Emitting JSON to flow: $json")
        importedQuestJsonFlow.tryEmit(json)
    }
    fun clearEmptyJson() {
        importedQuestJsonFlow.tryEmit(null)
    }


    suspend fun importQuestFromJson(json: String): String {
        val data = Json.decodeFromString<ImportedQuest>(json)
        val code = data.quest.code
        // Save quest and get new questId
        val newQuestId = questDao.insert(data.quest.copy(id = 0)).toInt()
        // Save questions for the new quest
        data.questions.forEach { q ->
            questionDao.insert(q.copy(id = 0, questId = newQuestId))
        }
        return code
    }

}
