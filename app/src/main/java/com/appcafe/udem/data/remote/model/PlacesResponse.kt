package com.appcafe.udem.data.remote.model

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    val places: List<PlaceResult>?
)

data class PlaceResult(
    val id: String,
    val displayName: DisplayName?,
    val formattedAddress: String?,
    val location: LatLngRemote?,
    val rating: Double?,
    val userRatingCount: Int?,
    val photos: List<PlacePhoto>?,
    val regularOpeningHours: OpeningHours?,
    val internationalPhoneNumber: String?,
    val websiteUri: String?
)

data class DisplayName(
    val text: String?,
    val languageCode: String?
)

data class LatLngRemote(
    val latitude: Double,
    val longitude: Double
)

data class PlacePhoto(
    val name: String?
)

data class OpeningHours(
    val openNow: Boolean?,
    val weekdayDescriptions: List<String>?
)
