package com.mek.weatherapp.domain.model

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val pop: Int,                  // yağış ihtimali
    val icon: String
)
