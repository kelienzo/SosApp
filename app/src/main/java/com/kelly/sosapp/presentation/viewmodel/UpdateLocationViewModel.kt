package com.kelly.sosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelly.sosapp.data.model.UpdateLocationRequest
import com.kelly.sosapp.data.model.UpdateLocationResponse
import com.kelly.sosapp.data.repository.UpdateLocationRepository
import com.kelly.sosapp.presentation.screens.CameraScreenEvent
import com.kelly.sosapp.presentation.viewmodel.state.UpdateLocationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class UpdateLocationViewModel @Inject constructor(
    private val updateLocationRepository: UpdateLocationRepository
) : ViewModel() {

    private val _isLoading = Channel<Boolean>()
    val isLoading = _isLoading.receiveAsFlow()

    private val _updateLocationUiState = Channel<UpdateLocationUiState>()
    val updateLocationUiState = _updateLocationUiState.receiveAsFlow()

    fun onEvent(event: CameraScreenEvent) {
        if (event is CameraScreenEvent.OnSendLocationDetails) {
            updateLocation(updateLocationRequest = event.locationUpdateRequest)
        }
    }

    private fun updateLocation(
        updateLocationRequest: UpdateLocationRequest
    ) {
        updateLocationRepository.updateLocation(updateLocationRequest = updateLocationRequest)
            .onStart {
                _isLoading.trySend(true)
            }.onEach {
                _updateLocationUiState.trySend(it)
                _isLoading.trySend(false)
            }.launchIn(viewModelScope)
    }
}