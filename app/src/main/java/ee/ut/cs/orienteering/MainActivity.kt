package ee.ut.cs.orienteering

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import ee.ut.cs.orienteering.ui.navigation.AppNavHost
import ee.ut.cs.orienteering.ui.theme.OrienteeringTheme
import ee.ut.cs.orienteering.ui.viewmodels.JoinLobbyViewModel
import kotlinx.coroutines.launch

/**
 * Main entry point of the application.
 *
 * Handles:
 * - Processing QR-code deep links for quest import
 * - Setting up the main UI content
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState Previously saved stat, or `null` if none exists.
     *
     * Initializes the UI and processes any incoming QR import requests.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleQrImportIntent(intent)

        setContent {
            OrienteeringTheme {
                MainScreen()
            }
        }
    }

    /**
     * Called when the activity receives a new intent while already running.
     *
     * @param intent The new intent delivered to the activity.
     *
     * Used to handle QR-code deep links when the activity is already in memory.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleQrImportIntent(intent)
    }


    private val joinLobbyViewModel: JoinLobbyViewModel by viewModels()

    /**
     * Processes a QR-code deep link intent and attempts to import a quest.
     *
     * Expected URI format:
     * `qrexport://quest?data=<base64-json>`
     *
     * @param intent The incoming intent that may contain a QR deep link.
     *
     * @throws IllegalArgumentException If the Base64 data is malformed.
     * @throws Exception If quest import fails inside [JoinLobbyViewModel.importQuestFromJson].
     */
    private fun handleQrImportIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme == "qrexport" && uri.host == "quest") {
            val base64 = uri.getQueryParameter("data") ?: return
            val json = String(Base64.decode(base64, Base64.DEFAULT))
            lifecycleScope.launch {
                try {
                    val questCode = joinLobbyViewModel.importQuestFromJson(json)

                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Quest Code", questCode)
                    clipboard.setPrimaryClip(clip)

                    Toast.makeText(
                        this@MainActivity,
                        "Quest imported! Code: $questCode (copied to clipboard)",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (_: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to import quest",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Root composable for the application's navigation structure.
     *
     * Sets up the navigation host and provides padding from the Scaffold.
    */
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
            AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }}
