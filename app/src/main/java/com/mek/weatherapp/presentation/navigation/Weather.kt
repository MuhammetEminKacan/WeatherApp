package com.mek.weatherapp.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Weather : NavKey

@Serializable
data class Detail(
    val dayIndex: Int
) : NavKey