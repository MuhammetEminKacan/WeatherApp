package com.mek.weatherapp.presentation.states

import com.mek.weatherapp.domain.model.DailyForecast

data class WeatherDetailState(
    val isLoading: Boolean = false,
    val dailyForecast: DailyForecast? = null
)