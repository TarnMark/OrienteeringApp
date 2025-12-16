package ee.ut.cs.orienteering

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.postDelayed

/**
 * Simple splash screen activity shown briefly before launching the main UI.
 *
 * Responsibilities:
 * - Display a static splash layout
 * - Delay for a short duration
 * - Navigate to [MainActivity] and close itself
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState Previously saved state, or `null` if none exists.
     *
     * Loads the splash layout and schedules a delayed transition to [MainActivity].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash)

        window.decorView.postDelayed(1200) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
