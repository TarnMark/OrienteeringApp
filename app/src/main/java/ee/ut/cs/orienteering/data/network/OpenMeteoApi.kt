package ee.ut.cs.orienteering.data.network

import ee.ut.cs.orienteering.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API definition for the Open‑Meteo weather service.
 *
 * This interface provides access to the `/v1/forecast` endpoint, requesting
 * only the current weather data for a given latitude and longitude.
 *
 * @see <https://open-meteo.com/> for API documentation.
 */
interface OpenMeteoApi {

    /**
     * Fetches the current weather for the specified geographic coordinates.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @param currentWeather Whether to include the `current_weather` block in the response.
     *
     * @return A [WeatherResponse] containing the current weather data, or `null` fields
     *         if the API does not return them.
     */
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): WeatherResponse
}