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
import ee.ut.cs.orienteering.ui.fakes.FakeCreateLobbyViewModel
import ee.ut.cs.orienteering.ui.screens.CreateLobbyScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

/**
 * UI tests for the Create Lobby screen.
 *
 * Purpose:
 * - Verifies that entering a valid lobby name and tapping the "Create Lobby" button
 *   triggers navigation to the map screen.
 * - Uses [FakeCreateLobbyViewModel] to avoid real database operations and to
 *   control the returned quest ID.
 * - Uses a [TestNavHostController] to assert navigation destinations.
 *
 * Test flow:
 * 1. Render the CreateLobbyScreen inside a test NavHost
 * 2. Enter a lobby name into the text field
 * 3. Click the "Create Lobby" button
 * 4. Assert that navigation occurred with the expected quest ID
 */
@RunWith(AndroidJUnit4::class)
class CreateLobbyScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    /** * Sets up the Compose content with a test NavHost and the provided fake ViewModel. */
    private fun setContent(fakeViewModel: FakeCreateLobbyViewModel) {
        val application = ApplicationProvider.getApplicationContext<Application>()
        navController = TestNavHostController(application).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }

        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "createLobby"
            ) {
                composable("createLobby") {
                    CreateLobbyScreen(
                        navController = navController,
                        viewModel = fakeViewModel
                    )
                }
                composable("map/true/{id}") { }
            }
        }
    }

    /**
     * Ensures that when the user enters a valid lobby name and taps the create button,
     * the screen navigates to the map route with the correct quest ID.
     */
    @Test
    fun givenValidInput_whenCreateLobbyClicked_thenNavigatesToMap() {
        val fakeViewModel = FakeCreateLobbyViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeQuestDao()
        )

        setContent(fakeViewModel)

        composeTestRule.onNodeWithText("Lobby name").performTextInput("Test Lobby")

        composeTestRule.onNode(hasText("Create Lobby") and hasClickAction())
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnUiThread {
            val idArg = navController.currentBackStackEntry?.arguments?.getString("id")
            assertEquals("123", idArg)
        }
    }
}
