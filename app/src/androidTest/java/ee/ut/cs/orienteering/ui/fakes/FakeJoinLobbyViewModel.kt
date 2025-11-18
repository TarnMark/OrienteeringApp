package ee.ut.cs.orienteering.ui.fakes

import android.app.Application
import ee.ut.cs.orienteering.data.FakeQuestDao
import ee.ut.cs.orienteering.ui.viewmodels.JoinLobbyViewModel
import ee.ut.cs.orienteering.ui.viewmodels.LobbyUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class FakeJoinLobbyViewModel(
    application: Application,
    private val questDao: FakeQuestDao
) : JoinLobbyViewModel(application) {

    var joinLobbyCalled = false
    var joinedCode: String? = null
    var returnedQuestId: Int = 123

    private val _fakeState = MutableStateFlow(LobbyUiState())
    override val state: StateFlow<LobbyUiState> = _fakeState

    override val importedQuestJsonFlow = MutableSharedFlow<String?>(extraBufferCapacity = 1)

    override fun joinLobby(code: String, onSuccess: (questId: Int) -> Unit): Job {
        joinLobbyCalled = true
        joinedCode = code

        return if (questDao.returnNull) {
            _fakeState.value = LobbyUiState(errorMessage = "Invalid lobby code")
            Job()
        } else {
            onSuccess(returnedQuestId)
            _fakeState.value = LobbyUiState()
            Job()
        }
    }

    override fun emitImportedJson(json: String) {
        importedQuestJsonFlow.tryEmit(json)
    }

    override fun clearEmptyJson() {
        importedQuestJsonFlow.tryEmit(null)
    }
}