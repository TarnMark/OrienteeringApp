package ee.ut.cs.orienteering

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.Quest
import ee.ut.cs.orienteering.data.Question
import ee.ut.cs.orienteering.ui.navigation.AppNavHost
import ee.ut.cs.orienteering.ui.theme.OrienteeringTheme
import ee.ut.cs.orienteering.ui.viewmodels.JoinLobbyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("seed_prefs", MODE_PRIVATE)
        val done = prefs.getBoolean("seed_done_v1", false)

        if (!done) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val db = AppDatabase.getDatabase(this@MainActivity)
                    val qDao = db.questionDao()
                    val questDao = db.questDao()

                    val questId =
                        if (questDao.count() == 0) {
                            questDao.insert(
                                Quest(
                                    id = 0,
                                    title = "Sample Quest",
                                    code = "demo"
                                )
                            ).toInt()
                        } else 0

                    if (qDao.count() == 0) {
                        val sample = listOf(
                            Question(0, questId, "What color is the flower pot?", "red", "58.384785, 26.721060"),
                            Question(0, questId, "How many computers can you see?", "17", "58.385501,26.725032"),
                            Question(0, questId, "What year was it built?", "2031", "58.380662, 26.725357"),
                            Question(0, questId, "How many zebra stripes?", "11", "58.377636, 26.729277"),
                            Question(0, questId, "Whose monument?", "Eduard Tubin", "58.376659, 26.725145")
                        )
                        qDao.insertAll(sample)
                    }

                } catch (t: Throwable) {
                    Log.e("DB_SEED", "Seed failed", t)
                }
            }
        }

        handleQrImportIntent(intent)

        setContent {
            OrienteeringTheme {
                MainScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleQrImportIntent(intent)
    }


    private val joinLobbyViewModel: JoinLobbyViewModel by viewModels()
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
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
            AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }}
