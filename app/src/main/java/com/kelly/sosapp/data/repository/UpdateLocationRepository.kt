package com.kelly.sosapp.data.repository

import com.google.gson.Gson
import com.kelly.sosapp.data.model.UpdateLocationRequest
import com.kelly.sosapp.data.model.UpdateLocationResponse
import com.kelly.sosapp.data.remote.SosService
import com.kelly.sosapp.presentation.viewmodel.state.UpdateLocationUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateLocationRepository @Inject constructor(private val sosService: SosService) {


    fun updateLocation(updateLocationRequest: UpdateLocationRequest) = flow {
        val response = sosService.updateLocation(updateLocationRequest = updateLocationRequest)

        if (response.isSuccessful) {
            response.body()?.let { res ->
                if (res.status == "success") {
                    emit(UpdateLocationUiState(updateLocationResponse = res))
                } else {
                    emit(UpdateLocationUiState(errorMessage = res.message))
                }
            }
        } else {
            val error = Gson().fromJson(
                response.errorBody()?.string(),
                UpdateLocationResponse::class.java
            )
            emit(UpdateLocationUiState(errorMessage = error.message))
        }
    }.catch {
        it.printStackTrace()
        emit(UpdateLocationUiState(errorMessage = it.message))
    }.flowOn(Dispatchers.IO)
}