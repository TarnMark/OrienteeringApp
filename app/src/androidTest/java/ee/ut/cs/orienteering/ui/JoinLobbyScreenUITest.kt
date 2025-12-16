package ee.ut.cs.orienteering.ui

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ee.ut.cs.orienteering.data.FakeQuestDao
import ee.ut.cs.orienteering.ui.fakes.FakeJoinLobbyViewModel
import ee.ut.cs.orienteering.ui.screens.JoinLobbyScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

/**
 * UI tests for the Join Lobby screen.
 *
 * Purpose:
 * - Verifies that entering a valid lobby code and tapping the "Join" button
 *   navigates to the map screen with the correct quest ID.
 * - Verifies that entering an invalid code displays an appropriate error message.
 * - Uses [FakeJoinLobbyViewModel] to simulate both success and failure scenarios
 *   without touching the real database.
 * - Uses a [TestNavHostController] to assert navigation behavior.
 *
 * Test flow:
 * 1. Render the JoinLobbyScreen inside a test NavHost
 * 2. Enter a lobby code into the text field
 * 3. Click the "Join" button
 * 4. Assert navigation or error state depending on the fake DAO configuration
 */
@RunWith(AndroidJUnit4::class)
class JoinLobbyScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    /**
     * Sets up the Compose content with a test NavHost and the provided fake ViewModel.
     */
    private fun setContent(fakeViewModel: FakeJoinLobbyViewModel) {
        val application = ApplicationProvider.getApplicationContext<Application>()
        navController = TestNavHostController(application).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }

        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "joinLobby"
            ) {
                composable("joinLobby") {
                    JoinLobbyScreen(
                        navController = navController,
                        viewModel = fakeViewModel
                    )
                }
                composable("map/false/{id}") { }
            }
        }
    }

    /**
     * Ensures that when the user enters a valid lobby code and taps the join button,
     * the screen navigates to the map route with the expected quest ID.
     */
    @Test
    fun givenValidCode_whenJoinLobbyClicked_thenNavigatesToMap() {
        val fakeViewModel = FakeJoinLobbyViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeQuestDao()
        )

        setContent(fakeViewModel)

        composeTestRule.onNodeWithText("Lobby code").performTextInput("VALID_CODE")

        composeTestRule.onNode(hasText("Join") and hasClickAction())
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnUiThread {
            val idArg = navController.currentBackStackEntry?.arguments?.getString("id")
            assertEquals("123", idArg)
        }
    }

    /**
     * Ensures that when the user enters an invalid lobby code,
     * an error message is displayed instead of navigating.
     */
    @Test
    fun givenInvalidCode_whenJoinLobbyClicked_thenShowsErrorMessage() {
        val fakeViewModel = FakeJoinLobbyViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeQuestDao(returnNull = true)
        )

        setContent(fakeViewModel)

        composeTestRule.onNodeWithText("Lobby code").performTextInput("INVALID_CODE")

        composeTestRule.onNode(hasText("Join") and hasClickAction())
            .assertIsEnabled()
            .performClick()

        composeTestRule.onNodeWithText("Invalid lobby code").assertExists()
    }
}
