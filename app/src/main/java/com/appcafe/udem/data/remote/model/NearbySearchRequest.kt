package com.appcafe.udem.data.remote.model

data class NearbySearchRequest(
    val includedTypes: List<String> = listOf("cafe"),
    val maxResultCount: Int = 20,
    val locationRestriction: LocationRestriction
)

data class LocationRestriction(
    val circle: Circle
)

data class Circle(
    val center: LatLngRequest,
    val radius: Double
)

data class LatLngRequest(
    val latitude: Double,
    val longitude: Double
)
