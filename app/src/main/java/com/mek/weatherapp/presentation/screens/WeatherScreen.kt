package com.mek.weatherapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mek.weatherapp.domain.model.DailyForecast
import com.mek.weatherapp.domain.model.TodayWeather
import com.mek.weatherapp.presentation.events.WeatherEvent
import com.mek.weatherapp.presentation.viewmodels.WeatherViewModel
import com.mek.weatherapp.presentation.states.WeatherUiState

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onLocationRequest: () -> Unit,
    registerLocationCallbacks: (
        onSuccess: (Double, Double) -> Unit,
        onFail: () -> Unit
    ) -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        registerLocationCallbacks(
            { lat, lon ->
                viewModel.onEvent(
                    WeatherEvent.LoadWeatherByLocation(lat, lon)
                )
            },
            {
                viewModel.onEvent(
                    WeatherEvent.SearchCity("Istanbul")
                )
            }
        )

        if (uiState.weather == null && !uiState.isLoading) {
            onLocationRequest()
        }
    }

    WeatherScreenContent(
        uiState = uiState,
        onSearchQueryChange = {
            viewModel.onEvent(WeatherEvent.UpdateSearchQuery(it))
        },
        onSearch = {
            viewModel.onEvent(WeatherEvent.SearchCity(uiState.searchQuery))
        },
        onLocationClick = onLocationRequest,
        onRetry = {
            viewModel.onEvent(WeatherEvent.Retry)
        },
        onDayClick = onNavigateToDetail
    )
}

@Composable
fun WeatherScreenContent(
    uiState: WeatherUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLocationClick: () -> Unit,
    onRetry: () -> Unit,
    onDayClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5A9FD4),
                        Color(0xFF82B5DB),
                        Color(0xFFA8CCE5)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            SearchBarSection(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = onSearch,
                onLocationClick = onLocationClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.errorMessage != null -> {
                    ErrorState(
                        message = uiState.errorMessage,
                        onRetry = onRetry
                    )
                }
                uiState.weather != null -> {
                    WeatherContent(
                        cityName = uiState.weather.cityName,
                        today = uiState.weather.today,
                        dailyForecasts = uiState.weather.dailyForecasts,
                        onDayClick = onDayClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = {
                Text(
                    "Şehir ara...",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF5A9FD4)
                        )
                    }
                }
            }
        )

        IconButton(
            onClick = onLocationClick,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Location",
                tint = Color(0xFF5A9FD4)
            )
        }
    }
}

@Composable
fun WeatherContent(
    cityName: String,
    today: TodayWeather,
    dailyForecasts: List<DailyForecast>,
    onDayClick: (Int) -> Unit
) {
    Column {
        TodayWeatherCard(
            cityName = cityName,
            today = today
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "5 Günlük Tahmin",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        DailyForecastList(
            dailyForecasts = dailyForecasts,
            onDayClick = onDayClick
        )
    }
}

@Composable
fun TodayWeatherCard(
    cityName: String,
    today: TodayWeather
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cityName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )

            Spacer(modifier = Modifier.height(24.dp))

            WeatherIcon(
                icon = today.icon,
                size = 120.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${today.temperature.toInt()}°",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = today.description,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5A9FD4)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Hissedilen ${today.feelsLike.toInt()}°",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nem: %${today.humidity}, Rüzgar: ${today.windSpeed} km/s",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DailyForecastList(
    dailyForecasts: List<DailyForecast>,
    onDayClick: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        itemsIndexed(dailyForecasts.take(5)) { index, forecast ->
            DailyForecastCard(
                forecast = forecast,
                dayIndex = index,
                onClick = onDayClick
            )
        }
    }
}

@Composable
fun DailyForecastCard(
    forecast: DailyForecast,
    dayIndex: Int,
    onClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = { onClick(dayIndex) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = forecast.date,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C3E50)
            )

            WeatherIcon(
                icon = forecast.icon,
                size = 48.dp
            )

            Text(
                text = "${forecast.maxTemp.toInt()}° - ${forecast.minTemp.toInt()}°",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C3E50)
            )

            forecast.hourlyForecasts.firstOrNull()?.let { hourly ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "💧", fontSize = 12.sp)
                    Text(
                        text = " ${hourly.pop}%",
                        fontSize = 12.sp,
                        color = Color(0xFF5A9FD4)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherIcon(icon: String, size: androidx.compose.ui.unit.Dp) {
    val iconUrl = "https://openweathermap.org/img/wn/${icon}@4x.png"

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(iconUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(size)
    )
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "❌", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5A9FD4)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Tekrar Dene")
            }
        }
    }
}
