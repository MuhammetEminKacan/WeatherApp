package com.mek.weatherapp.presentation.events

sealed class WeatherEvent {

    data class LoadWeatherByLocation(
        val lat: Double,
        val lon: Double
    ) : WeatherEvent()

    data class SearchCity(
        val cityName: String
    ) : WeatherEvent()

    object Retry : WeatherEvent()
}