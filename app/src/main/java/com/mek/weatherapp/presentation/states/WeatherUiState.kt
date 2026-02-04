package com.mek.weatherapp.presentation.states

import com.mek.weatherapp.domain.model.WeatherForecast

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherForecast? = null,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val lastSearchedCity: String? = null,
    val lastLocation: Pair<Double, Double>? = null
)