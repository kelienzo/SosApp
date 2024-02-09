package com.kelly.sosapp.data.remote

import com.kelly.sosapp.data.model.UpdateLocationRequest
import com.kelly.sosapp.data.model.UpdateLocationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SosService {

    @POST("create")
    suspend fun updateLocation(
        @Body updateLocationRequest: UpdateLocationRequest
    ): Response<UpdateLocationResponse>
}