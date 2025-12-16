package ee.ut.cs.orienteering.ui.fakes

import android.app.Application
import ee.ut.cs.orienteering.data.FakeQuestDao
import ee.ut.cs.orienteering.ui.viewmodels.CreateLobbyViewModel

/**
 * Fake implementation of [CreateLobbyViewModel] used for UI tests.
 *
 * Purpose:
 * - Captures calls to [createLobby] without touching the real database
 * - Records the provided title and code for assertion in tests
 * - Immediately invokes the callback with a predefined lobby ID
 *
 * Test usage:
 * - Verify that `createLobby` was called
 * - Assert that the correct title and code were passed
 * - Control the returned quest ID via [returnedId]
 *
 * @param application The application context required by the base ViewModel.
 * @param questDao A fake DAO used to satisfy the parent constructor.
 */
class FakeCreateLobbyViewModel(
    application: Application,
    questDao: FakeQuestDao
) : CreateLobbyViewModel(application, questDao) {

    /** Whether [createLobby] was invoked during the test. */
    var createLobbyCalled = false
    /** The title passed to [createLobby]. */
    var createdTitle: String? = null
    /** The code passed to [createLobby]. */
    var createdCode: String? = null
    /** The fake quest ID returned to the UI when [createLobby] is called. */
    var returnedId: Int = 123

    /**
     * Overrides the real lobby creation logic with a test‑friendly implementation.
     *
     * Instead of inserting into the database, this:
     * - Marks the call as executed
     * - Stores the provided parameters
     * - Immediately invokes [onCreated] with [returnedId]
     */
    override fun createLobby(title: String, codeInput: String?, onCreated: (Int) -> Unit) {
        createLobbyCalled = true
        createdTitle = title
        createdCode = codeInput

        onCreated(returnedId)
    }
}
