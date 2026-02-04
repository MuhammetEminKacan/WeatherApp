package com.mek.weatherapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mek.weatherapp.domain.usecase.GetForecastByCityUseCase
import com.mek.weatherapp.domain.usecase.GetForecastByLocationUseCase
import com.mek.weatherapp.presentation.events.WeatherEvent
import com.mek.weatherapp.presentation.states.WeatherUiState
import com.mek.weatherapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getForecastByCityUseCase: GetForecastByCityUseCase,
    private val getForecastByLocationUseCase: GetForecastByLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun onEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.LoadWeatherByLocation -> {
                loadWeatherByLocation(event.lat, event.lon)
            }

            is WeatherEvent.SearchCity -> {
                if (event.cityName.isNotBlank()) {
                    searchByCity(event.cityName)
                }
            }

            is WeatherEvent.UpdateSearchQuery -> {
                updateSearchQuery(event.query)
            }

            WeatherEvent.ClearError -> {
                clearError()
            }

            WeatherEvent.Retry -> {
                retry()
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun retry() {
        val currentState = _uiState.value

        when {
            currentState.lastSearchedCity != null -> {
                searchByCity(currentState.lastSearchedCity)
            }
            currentState.lastLocation != null -> {
                val (lat, lon) = currentState.lastLocation
                loadWeatherByLocation(lat, lon)
            }
        }
    }

    private fun loadWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            getForecastByLocationUseCase(lat, lon).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null,
                                lastLocation = Pair(lat, lon)
                            )
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                weather = result.data,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun searchByCity(cityName: String) {
        viewModelScope.launch {
            getForecastByCityUseCase(cityName).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null,
                                lastSearchedCity = cityName,
                                searchQuery = "" // Arama sorgusunu temizle
                            )
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                weather = result.data,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}