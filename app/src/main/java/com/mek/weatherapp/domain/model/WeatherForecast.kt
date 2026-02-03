package com.mek.weatherapp.domain.model

data class WeatherForecast(
    val cityName: String,
    val today: TodayWeather,
    val dailyForecasts: List<DailyForecast>
)
