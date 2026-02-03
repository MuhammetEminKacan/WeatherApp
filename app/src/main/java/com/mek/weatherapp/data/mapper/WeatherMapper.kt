package com.mek.weatherapp.data.mapper

import com.mek.weatherapp.data.remote.dtos.ForecastResponseDto
import com.mek.weatherapp.domain.model.DailyForecast
import com.mek.weatherapp.domain.model.HourlyForecast
import com.mek.weatherapp.domain.model.TodayWeather
import com.mek.weatherapp.domain.model.WeatherForecast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun ForecastResponseDto.toDomain(): WeatherForecast {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale("tr"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val groupedByDay = list.groupBy {
        LocalDateTime.parse(it.dt_txt, formatter).toLocalDate()
    }

    val todayEntries = groupedByDay.entries.first().value

    val today = todayEntries.first().let {
        TodayWeather(
            temperature = it.main.temp,
            feelsLike = it.main.feels_like,
            humidity = it.main.humidity,
            windSpeed = it.wind.speed,
            description = it.weather.first().description,
            icon = it.weather.first().icon
        )
    }

    val dailyForecasts = groupedByDay.map { (date, items) ->

        val minTemp = items.minOf { it.main.temp_min }
        val maxTemp = items.maxOf { it.main.temp_max }

        DailyForecast(
            date = date.format(dayFormatter),
            minTemp = minTemp,
            maxTemp = maxTemp,
            icon = items.first().weather.first().icon,
            hourlyForecasts = items.map { hourly ->
                HourlyForecast(
                    time = LocalDateTime
                        .parse(hourly.dt_txt, formatter)
                        .format(timeFormatter),
                    temperature = hourly.main.temp,
                    feelsLike = hourly.main.feels_like,
                    humidity = hourly.main.humidity,
                    windSpeed = hourly.wind.speed,
                    pop = (hourly.pop * 100).toInt(),
                    icon = hourly.weather.first().icon
                )
            }
        )
    }

    return WeatherForecast(
        cityName = city.name,
        today = today,
        dailyForecasts = dailyForecasts
    )
}