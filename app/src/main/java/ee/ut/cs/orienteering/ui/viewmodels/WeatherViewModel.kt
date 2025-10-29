package ee.ut.cs.orienteering.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.network.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repo: WeatherRepository) : ViewModel() {
    var weatherText = mutableStateOf<String?>(null)
        private set

    fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weather = repo.fetchWeather(lat, lon)
                weatherText.value = weather?.let {
                    "Temp: ${it.temperature}°C, Wind: ${it.windspeed} km/h"
                } ?: "No data"
            } catch (e: Exception) {
                weatherText.value = "Weather unavailable"
//                Log.e("Weather",e.message?:"Something went wrong")
            }
        }
    }
}
