package com.mek.weatherapp.presentation.events

import com.mek.weatherapp.domain.model.WeatherForecast

sealed class WeatherDetailEvent {

    data class LoadDay(
        val weatherForecast: WeatherForecast,
        val dayIndex: Int
    ) : WeatherDetailEvent()
}