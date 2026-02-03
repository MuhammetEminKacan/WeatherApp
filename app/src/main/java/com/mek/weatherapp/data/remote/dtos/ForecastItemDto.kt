package com.mek.weatherapp.data.remote.dtos

data class ForecastItemDto(
    val dt: Long,
    val main: MainDto,
    val weather: List<WeatherDto>,
    val clouds: CloudsDto,
    val wind: WindDto,
    val visibility: Int?,
    val pop: Double,
    val snow: SnowDto?,
    val sys: SysDto,
    val dt_txt: String
)
