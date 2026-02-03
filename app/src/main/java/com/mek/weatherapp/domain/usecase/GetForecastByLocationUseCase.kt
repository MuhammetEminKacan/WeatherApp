package com.mek.weatherapp.domain.usecase

import com.mek.weatherapp.domain.model.WeatherForecast
import com.mek.weatherapp.domain.repository.WeatherRepository
import com.mek.weatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForecastByLocationUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    operator fun invoke(
        lat: Double,
        lon: Double
    ): Flow<Resource<WeatherForecast>> {
        return repository.getForecastByLocation(lat, lon)
    }
}