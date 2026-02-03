package com.mek.weatherapp.data.remote.interceptor

import com.mek.weatherapp.utils.Constants.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class WeatherQueryInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("appid", API_KEY)
            .addQueryParameter("units", "metric")
            .addQueryParameter("lang", "tr")
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}