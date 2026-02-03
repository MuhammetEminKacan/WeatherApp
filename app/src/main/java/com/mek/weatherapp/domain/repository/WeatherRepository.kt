package com.mek.weatherapp.domain.repository

import com.mek.weatherapp.domain.model.WeatherForecast
import com.mek.weatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getForecastByCity(
        cityName: String
    ): Flow<Resource<WeatherForecast>>

    fun getForecastByLocation(
        lat: Double,
        lon: Double
    ): Flow<Resource<WeatherForecast>>
}