package com.mek.weatherapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mek.weatherapp.domain.model.DailyForecast
import com.mek.weatherapp.domain.model.HourlyForecast
import com.mek.weatherapp.domain.model.WeatherForecast
import com.mek.weatherapp.presentation.events.WeatherDetailEvent
import com.mek.weatherapp.presentation.viewmodels.WeatherDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    dayIndex: Int,
    weatherForecast: WeatherForecast,
    onBack: () -> Unit
) {
    val viewModel = remember { WeatherDetailViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            WeatherDetailEvent.LoadDay(
                weatherForecast = weatherForecast,
                dayIndex = dayIndex
            )
        )
    }

    val dailyForecast = uiState.dailyForecast

    if (uiState.isLoading || dailyForecast == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

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
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Geri butonu ve başlık
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = dailyForecast.date,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Üst özet kartı
            DailySummaryCard(dailyForecast = dailyForecast)

            Spacer(modifier = Modifier.height(24.dp))

            // Saatlik tahmin listesi
            HourlyForecastSection(hourlyForecasts = dailyForecast.hourlyForecasts)
        }
    }
}

@Composable
fun DailySummaryCard(dailyForecast: DailyForecast) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${dailyForecast.icon}@4x.png",
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = dailyForecast.description,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5A9FD4)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text("↑ ${dailyForecast.maxTemp.toInt()}°")
                Spacer(modifier = Modifier.width(16.dp))
                Text("↓ ${dailyForecast.minTemp.toInt()}°")
            }
        }
    }
}

@Composable
fun HourlyForecastSection(hourlyForecasts: List<HourlyForecast>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                .padding(20.dp)
        ) {
            Text(
                text = "Saatlik Tahmin",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Saatlik tahmin listesi
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.heightIn(max = 500.dp)
            ) {
                items(hourlyForecasts) { hourly ->
                    HourlyForecastItem(hourly = hourly)
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(hourly: HourlyForecast) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Saat
        Text(
            text = hourly.time,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2C3E50),
            modifier = Modifier.width(50.dp)
        )

        // Icon
        val iconUrl = "https://openweathermap.org/img/wn/${hourly.icon}@2x.png"
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(iconUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Weather icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )

        // Sıcaklık
        Text(
            text = "${hourly.temperature.toInt()}°",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50),
            modifier = Modifier.width(40.dp)
        )

        // Hissedilen
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "Hissedilen",
                fontSize = 10.sp,
                color = Color.Gray
            )
            Text(
                text = "${hourly.feelsLike.toInt()}°",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C3E50)
            )
        }

        // Nem
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = "💧",
                fontSize = 12.sp
            )
            Text(
                text = " ${hourly.humidity}%",
                fontSize = 12.sp,
                color = Color(0xFF5A9FD4)
            )
        }

        // Rüzgar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = "🌬",
                fontSize = 12.sp
            )
            Text(
                text = " ${hourly.windSpeed.toInt()}",
                fontSize = 12.sp,
                color = Color(0xFF5A9FD4)
            )
        }

        // Yağış ihtimali
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = "☔",
                fontSize = 12.sp
            )
            Text(
                text = " ${hourly.pop}%",
                fontSize = 12.sp,
                color = Color(0xFF5A9FD4)
            )
        }
    }
}