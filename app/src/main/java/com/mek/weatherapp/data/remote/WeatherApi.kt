package com.mek.weatherapp.data.remote

import com.mek.weatherapp.data.remote.dtos.ForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast")
    suspend fun getForecastByCity(
        @Query("q") cityName : String
    ) : ForecastResponseDto

    @GET("forecast")
    suspend fun getForecastByLocation(
        @Query("lat") latitude : Double,
        @Query("lon") longitude : Double
    ) : ForecastResponseDto

}