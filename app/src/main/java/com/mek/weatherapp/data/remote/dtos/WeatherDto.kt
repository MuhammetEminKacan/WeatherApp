package com.mek.weatherapp.data.remote.dtos

data class WeatherDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
