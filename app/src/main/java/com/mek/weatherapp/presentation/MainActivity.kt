package com.mek.weatherapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mek.weatherapp.presentation.screens.DetailScreen
import com.mek.weatherapp.presentation.screens.WeatherScreen
import com.mek.weatherapp.presentation.ui.theme.WeatherAppTheme
import com.mek.weatherapp.presentation.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var onLocationResult: ((Double, Double) -> Unit)? = null
    private var onLocationFailed: (() -> Unit)? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getCurrentLocation()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Konum izni reddedildi, İstanbul gösteriliyor",
                    Toast.LENGTH_SHORT
                ).show()
                onLocationFailed?.invoke()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.WeatherScreen.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.WeatherScreen.route) {
                            val viewModel: WeatherViewModel = hiltViewModel()

                            WeatherScreen(
                                viewModel = viewModel,
                                onLocationRequest = { requestLocation() },
                                registerLocationCallbacks = { onSuccess, onFail ->
                                    onLocationResult = onSuccess
                                    onLocationFailed = onFail
                                },
                                onNavigateToDetail = { dayIndex ->
                                    navController.navigate(
                                        Screen.DetailScreen.createRoute(dayIndex)
                                    )
                                }
                            )
                        }

                        composable(
                            route = Screen.DetailScreen.route,
                            arguments = listOf(
                                navArgument("dayIndex") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(Screen.WeatherScreen.route)
                            }

                            val viewModel: WeatherViewModel = hiltViewModel(parentEntry)
                            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 0

                            DetailScreen(
                                dayIndex = dayIndex,
                                navController = navController,
                                weatherForecast = viewModel.uiState.value.weather ?: return@composable
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestLocation() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            location?.let {
                onLocationResult?.invoke(it.latitude, it.longitude)
            } ?: onLocationFailed?.invoke()
        }.addOnFailureListener {
            onLocationFailed?.invoke()
        }
    }
}
