package ee.ut.cs.orienteering.data.network

import ee.ut.cs.orienteering.data.model.CurrentWeather

class WeatherRepository(private val api: OpenMeteoApi) {
    suspend fun fetchWeather(lat: Double, lon: Double): CurrentWeather? {
        return api.getCurrentWeather(lat, lon).currentWeather
    }
}