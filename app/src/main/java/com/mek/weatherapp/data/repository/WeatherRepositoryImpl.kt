package com.mek.weatherapp.data.repository

import retrofit2.HttpException
import android.util.Log
import com.mek.weatherapp.data.mapper.toDomain
import com.mek.weatherapp.data.remote.WeatherApi
import com.mek.weatherapp.domain.model.WeatherForecast
import com.mek.weatherapp.domain.repository.WeatherRepository
import com.mek.weatherapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override fun getForecastByCity(
        cityName: String
    ): Flow<Resource<WeatherForecast>> = flow {

        emit(Resource.Loading())

        try {
            val response = api.getForecastByCity(cityName)
            val domainModel = response.toDomain()
            emit(Resource.Success(domainModel))
        } catch (e: HttpException) {
            Log.e("WeatherRepo-City", e.toString())
            emit(Resource.Error("Bilinmeyen bir hata oluştu"))
        } catch (e: IOException) {
            Log.e("WeatherRepo-City", e.toString())
            emit(Resource.Error("İnternet bağlantısı yok"))
        }

    }.flowOn(Dispatchers.IO)

    override fun getForecastByLocation(
        lat: Double,
        lon: Double
    ): Flow<Resource<WeatherForecast>> = flow {

        emit(Resource.Loading())

        try {
            val response = api.getForecastByLocation(
                latitude = lat,
                longitude = lon
            )
            val domainModel = response.toDomain()
            emit(Resource.Success(domainModel))
        } catch (e: HttpException) {
            Log.e("WeatherRepo-Location", e.toString())
            emit(Resource.Error("Bilinmeyen bir hata oluştu"))
        } catch (e: IOException) {
            Log.e("WeatherRepo-Location", e.toString())
            emit(Resource.Error("İnternet bağlantısı yok"))
        }

    }.flowOn(Dispatchers.IO)
}