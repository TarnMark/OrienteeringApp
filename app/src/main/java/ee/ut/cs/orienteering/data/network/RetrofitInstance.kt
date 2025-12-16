package ee.ut.cs.orienteering.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton provider for the Retrofit instance used to access the Open‑Meteo API.
 *
 * Responsibilities:
 * - Lazily creates a configured [Retrofit] client
 * - Exposes a single shared instance of [OpenMeteoApi]
 * - Uses Gson for JSON deserialization
 *
 * The base URL points to the Open‑Meteo public weather API.
 */
object RetrofitInstance {

    /**
     * Lazily initialized API interface for performing weather requests.
     */
    val api: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }
}
