package ee.ut.cs.orienteering

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.postDelayed

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash)

        window.decorView.postDelayed(1200) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
