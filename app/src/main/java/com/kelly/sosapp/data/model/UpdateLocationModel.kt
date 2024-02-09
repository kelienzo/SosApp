package com.kelly.sosapp.data.model

data class UpdateLocationRequest(
    val image: String,
    val location: Location,
    val phoneNumbers: List<String>
)

data class UpdateLocationResponse(
    val `data`: Data?,
    val message: String?,
    val status: String?
)

data class Location(
    val latitude: String,
    val longitude: String
)

data class Data(
    val id: Int?,
    val image: String?,
    val location: Location?,
    val phoneNumbers: List<String>?
)