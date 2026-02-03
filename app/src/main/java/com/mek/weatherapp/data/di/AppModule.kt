package com.mek.weatherapp.data.di

import com.mek.weatherapp.data.remote.WeatherApi
import com.mek.weatherapp.data.remote.interceptor.WeatherQueryInterceptor
import com.mek.weatherapp.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideWeatherQueryInterceptor(): WeatherQueryInterceptor {
        return WeatherQueryInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        weatherQueryInterceptor: WeatherQueryInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(weatherQueryInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(
        retrofit: Retrofit
    ): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }
}