package ee.ut.cs.orienteering.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.network.WeatherRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for loading and exposing weather information
 * for a given geographic location.
 *
 * Responsibilities:
 * - Fetch weather data from [WeatherRepository]
 * - Format the result into a short, human‑readable text string
 * - Expose the formatted weather text via [weatherText] as Compose state
 *
 * This ViewModel is used by map‑related screens to show quick weather info
 * when the user taps a location.
 */
class WeatherViewModel(private val repo: WeatherRepository) : ViewModel() {

    /**
     * Holds the formatted weather text or `null` when no data is available.
     * Updated automatically when [loadWeather] is called.
     */
    var weatherText = mutableStateOf<String?>(null)
        private set

    /**
     * Loads weather data for the given coordinates and updates [weatherText].
     *
     * Behavior:
     * - On success: displays temperature and wind speed
     * - On failure: displays a fallback "Weather unavailable" message
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     */
    fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weather = repo.fetchWeather(lat, lon)
                weatherText.value = weather?.let {
                    "Temp: ${it.temperature}°C, Wind: ${it.windspeed} km/h"
                } ?: "No data"
            } catch (_: Exception) {
                weatherText.value = "Weather unavailable"
            }
        }
    }
}
