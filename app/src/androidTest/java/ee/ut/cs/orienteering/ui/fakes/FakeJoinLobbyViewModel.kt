package ee.ut.cs.orienteering.ui.fakes

import android.app.Application
import ee.ut.cs.orienteering.data.FakeQuestDao
import ee.ut.cs.orienteering.ui.viewmodels.JoinLobbyViewModel
import ee.ut.cs.orienteering.ui.viewmodels.LobbyUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Fake implementation of [JoinLobbyViewModel] used for UI tests.
 *
 * Purpose:
 * - Intercepts calls to [joinLobby] without performing real database operations
 * - Records the provided lobby code for assertions
 * - Allows tests to simulate both success and failure cases
 * - Exposes a controllable fake [state] for UI verification
 *
 * Behavior:
 * - If [FakeQuestDao.returnNull] is true, the join attempt fails and an error
 *   message is pushed into the fake state.
 * - Otherwise, the join succeeds immediately and invokes [onSuccess] with
 *   [returnedQuestId].
 *
 * Test usage:
 * - Assert that `joinLobbyCalled` is true
 * - Assert that `joinedCode` matches the expected input
 * - Control the outcome by toggling `questDao.returnNull`
 * - Inspect [state] to verify UI error handling
 *
 * @param application The application context required by the base ViewModel.
 * @param questDao A fake DAO used to simulate database responses.
 */
class FakeJoinLobbyViewModel(
    application: Application,
    private val questDao: FakeQuestDao
) : JoinLobbyViewModel(application) {

    /** Whether [joinLobby] was invoked during the test. */
    var joinLobbyCalled = false
    /** The lobby code passed to [joinLobby]. */
    var joinedCode: String? = null
    /** The fake quest ID returned on successful join. */
    var returnedQuestId: Int = 123

    private val _fakeState = MutableStateFlow(LobbyUiState())
    /** Exposed fake UI state used for testing error and loading behavior. */
    override val state: StateFlow<LobbyUiState> = _fakeState

    /**
     * Overrides the real join logic with a deterministic test implementation.
     *
     * Instead of querying the database, this:
     * - Marks the call as executed
     * - Stores the provided code
     * - Either triggers an error state or calls [onSuccess] immediately
     */
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
}