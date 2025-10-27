package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LobbyUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class JoinLobbyViewModel(app: Application) : AndroidViewModel(app) {

    private val questDao = AppDatabase.getDatabase(app).questDao()

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
}
