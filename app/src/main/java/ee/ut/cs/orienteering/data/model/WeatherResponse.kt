package ee.ut.cs.orienteering.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the top‑level response returned by the weather API.
 *
 * @property currentWeather The current weather data, or `null` if the API did not return it.
 *
 * This model maps the JSON field `"current_weather"` to [currentWeather].
 */
data class WeatherResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeather?
)

/**
 * Contains the current weather conditions for a specific location.
 *
 * @property temperature The temperature in degrees Celsius.
 * @property windspeed The wind speed in km/h.
 * @property weathercode A numeric weather condition code defined by the API.
 *
 * Used by [WeatherViewModel] to format weather information for display.
 */
data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val weathercode: Int
)
