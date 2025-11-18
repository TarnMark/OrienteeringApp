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

@RunWith(AndroidJUnit4::class)
class CreateLobbyScreenUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

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
