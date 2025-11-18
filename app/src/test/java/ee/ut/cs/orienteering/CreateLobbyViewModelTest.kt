package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import ee.ut.cs.orienteering.data.FakeQuestDao
import ee.ut.cs.orienteering.data.Quest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit test for CreateLobbyViewModel.
 *
 * Goal of this test:
 * - Verify that ensureUniqueCode() does NOT return a code that is already taken.
 * - Ensure that the generated code:
 *   - is different from the existing one,
 *   - has length 5,
 *   - consists only of A–Z and 0–9 characters.
 */
class CreateLobbyViewModelTest {

    private lateinit var viewModel: CreateLobbyViewModel
    private lateinit var fakeDao: FakeQuestDao

    /**
     * Common setup method run before each test.
     *
     * We:
     * - create a plain Application instance
     * - create FakeQuestDao (in-memory DAO),
     * - create CreateLobbyViewModel using the test constructor
     *   that accepts QuestDao directly.
     */
    @Before
    fun setup() {
        val app = Application()          // Allowed in local unit tests (no Android runtime needed)
        fakeDao = FakeQuestDao()
        viewModel = CreateLobbyViewModel(app, fakeDao) // Use the test-specific constructor.
    }

    /**
     * Test scenario:
     *
     * GIVEN: there is already a Quest with code "ABC12" in the "database".
     * WHEN:  we call ensureUniqueCode("ABC12").
     * THEN:  the returned code:
     *        - is not "ABC12",
     *        - has length 5,
     *        - matches the regex [A-Z0-9]{5}.
     *
     * This proves that ensureUniqueCode() actually checks for collisions
     * and generates a different code when the input is already taken.
     */
    @Test
    fun `ensureUniqueCode generates new code when input code already exists`() = runBlocking {
        // Arrange (GIVEN): our in-memory DAO already contains a Quest with code "ABC12".
        fakeDao.insert(
            Quest(
                id = 1,
                title = "Existing lobby",
                code = "ABC12"
            )
        )

        val result = viewModel.ensureUniqueCode("ABC12")

        assertNotEquals(
            "Generated code must NOT be equal to an already existing code",
            "ABC12",
            result
        )

        assertEquals(
            "Generated code must have length 5",
            5,
            result.length
        )

        assertTrue(
            "Generated code must contain only A–Z and 0–9 characters",
            result.matches(Regex("^[A-Z0-9]{5}$"))
        )
    }
}
