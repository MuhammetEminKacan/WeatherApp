package com.mek.weatherapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.mek.weatherapp.presentation.events.WeatherDetailEvent
import com.mek.weatherapp.presentation.states.WeatherDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherDetailState())
    val uiState: StateFlow<WeatherDetailState> = _uiState.asStateFlow()

    fun onEvent(event: WeatherDetailEvent) {
        when (event) {
            is WeatherDetailEvent.LoadDay -> {
                loadDay(event.weatherForecast, event.dayIndex)
            }
        }
    }

    private fun loadDay(
        weatherForecast: com.mek.weatherapp.domain.model.WeatherForecast,
        dayIndex: Int
    ) {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }

        val dailyForecast = weatherForecast
            .dailyForecasts
            .getOrNull(dayIndex)

        _uiState.update {
            it.copy(
                isLoading = false,
                dailyForecast = dailyForecast
            )
        }
    }
}