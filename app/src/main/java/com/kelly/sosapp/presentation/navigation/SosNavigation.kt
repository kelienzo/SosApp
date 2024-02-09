package com.kelly.sosapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kelly.sosapp.presentation.screens.CameraScreen
import com.kelly.sosapp.presentation.screens.CameraScreenEvent
import com.kelly.sosapp.presentation.screens.HomeScreen
import com.kelly.sosapp.presentation.viewmodel.UpdateLocationViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SOSNavHost() {
    val navController = rememberNavController()

    var imageResponse by remember {
        mutableStateOf<Pair<String?, String?>?>(null)
    }

    NavHost(navController = navController, startDestination = "home_screen") {
        composable(
            route = "home_screen"
        ) {
            HomeScreen(
                image = imageResponse,
                onOpenCamera = {
                    navController.navigate("camera_screen")
                }
            )
        }

        composable(
            route = "camera_screen"
        ) {

            val viewModel = hiltViewModel<UpdateLocationViewModel>()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)

            LaunchedEffect(key1 = viewModel.updateLocationUiState) {
                viewModel.updateLocationUiState.collectLatest { response ->
                    response.updateLocationResponse?.let {
                        imageResponse = Pair(it.data?.image, it.message)
                        navController.popBackStack(route = "home_screen", inclusive = false)
                    } ?: response.errorMessage?.let {
                        imageResponse = Pair(null, it)
                    }
                }
            }

            CameraScreen(
                isLoading = isLoading,
                onUiEvent = { event ->
                    viewModel.onEvent(event)
                }
            )
        }
    }
}