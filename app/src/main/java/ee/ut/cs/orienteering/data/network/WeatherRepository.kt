package ee.ut.cs.orienteering.data.network

import ee.ut.cs.orienteering.data.model.CurrentWeather

/**
 * Repository responsible for fetching weather data from the Open‑Meteo API.
 *
 * This class provides a simple abstraction over the Retrofit service, exposing
 * only the data needed by the UI layer. It retrieves the current weather for
 * the given coordinates and returns it as a [CurrentWeather] model.
 *
 * @param api The Retrofit API interface used to perform network requests.
 */
class WeatherRepository(private val api: OpenMeteoApi) {

    /**
     * Fetches the current weather for the specified latitude and longitude.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return The [CurrentWeather] object returned by the API, or `null` if unavailable.
     */
    suspend fun fetchWeather(lat: Double, lon: Double): CurrentWeather? {
        return api.getCurrentWeather(lat, lon).currentWeather
    }
}