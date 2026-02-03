package com.mek.weatherapp.domain.model

data class DailyForecast(
    val date: String,              // Pazartesi,Salı…
    val minTemp: Double,
    val maxTemp: Double,
    val icon: String,
    val hourlyForecasts: List<HourlyForecast>
)
