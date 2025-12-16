package ee.ut.cs.orienteering.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ee.ut.cs.orienteering.data.network.WeatherRepository

/**
 * Factory for creating instances of [WeatherViewModel] with a required
 * [WeatherRepository] dependency.
 *
 * This is used when a ViewModel needs constructor parameters and therefore
 * cannot be created by the default `viewModel()` provider.
 *
 * @param repo The repository used by [WeatherViewModel] to fetch weather data.
 */
class WeatherViewModelFactory(private val repo: WeatherRepository) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of [WeatherViewModel] when requested by the
     * ViewModelProvider.
     *
     * @throws IllegalArgumentException if the requested ViewModel class is not supported.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
