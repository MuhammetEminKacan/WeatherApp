package com.mek.weatherapp.presentation

sealed class Screen(val route : String) {
    object WeatherScreen : Screen(route = "weather_screen")
    object DetailScreen : Screen("detail_screen/{dayIndex}") {
        fun createRoute(dayIndex: Int) = "detail_screen/$dayIndex"
    }
}