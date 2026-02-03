package com.mek.weatherapp.data.remote.dtos

data class ForecastResponseDto(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItemDto>,
    val city: CityDto
)
