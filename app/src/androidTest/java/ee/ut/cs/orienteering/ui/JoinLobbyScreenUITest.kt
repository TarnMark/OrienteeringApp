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

@RunWith(AndroidJUnit4::class)
class JoinLobbyScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

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
