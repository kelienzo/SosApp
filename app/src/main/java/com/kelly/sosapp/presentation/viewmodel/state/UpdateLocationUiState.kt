package com.kelly.sosapp.presentation.viewmodel.state

import com.kelly.sosapp.data.model.UpdateLocationResponse

data class UpdateLocationUiState(
    val updateLocationResponse: UpdateLocationResponse? = null,
    val errorMessage: String? = null
)
