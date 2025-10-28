package ee.ut.cs.orienteering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // val questions = SeedLoader.loadQuestions(this)
        //val checkpoints = SeedLoader.loadCheckpoints(this)

        //Log.d("TEST", "Questions loaded: ${questions.size}")
        //Log.d("TEST", "Checkpoints loaded: ${checkpoints.size}")
        val prefs = getSharedPreferences("seed_prefs", MODE_PRIVATE)
        val done = prefs.getBoolean("seed_done_v1", false)

        if (!done) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val db = AppDatabase.getDatabase(this@MainActivity)
                    val qDao = db.questionDao()
                    val questDao = db.questDao()

                    val questId = questDao.insert(
                        Quest(
                            id = 0,
                            title = "Sample Quest",
                            code = "demo"
                        )
                    ).toInt()

                    if (qDao.count() == 0) {
                        val sample = listOf(
                            Question(0, questId, "What is the capital of Finland?", "", ""),
                            Question(0, questId, "Name one essential item used in orienteering besides a compass.", "", ""),
                            Question(0, questId, "How long is a full marathon (km)?", "", ""),
                            Question(0, questId, "From which direction does the sun rise?", "", ""),
                            Question(0, questId, "Write the abbreviation of Global Positioning System.", "", "")
                        )
                        qDao.insertAll(sample)
                    }

                } catch (t: Throwable) {
                    android.util.Log.e("DB_SEED", "Seed failed", t)
                }
            }
        }


        setContent {
            OrienteeringTheme {
                MainScreen()
            }
        }
    }
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()

        Scaffold(
            //bottomBar = { NavigationBar(navController) }
        ) { innerPadding ->
            AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }}
