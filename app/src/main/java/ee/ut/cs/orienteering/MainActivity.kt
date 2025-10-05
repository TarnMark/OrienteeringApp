package ee.ut.cs.orienteering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import ee.ut.cs.orienteering.ui.components.NavigationBar
import ee.ut.cs.orienteering.ui.navigation.AppNavHost
import android.util.Log
import ee.ut.cs.orienteering.data.SeedLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val questions = SeedLoader.loadQuestions(this)
        val checkpoints = SeedLoader.loadCheckpoints(this)

        Log.d("TEST", "Questions loaded: ${questions.size}")
        Log.d("TEST", "Checkpoints loaded: ${checkpoints.size}")

        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { NavigationBar(navController) }
    ) { innerPadding ->
        AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
