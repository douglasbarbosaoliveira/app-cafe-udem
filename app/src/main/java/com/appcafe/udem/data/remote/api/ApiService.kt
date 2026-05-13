package com.appcafe.udem.data.remote.api

import com.appcafe.udem.data.remote.model.NearbySearchRequest
import com.appcafe.udem.data.remote.model.PlaceResult
import com.appcafe.udem.data.remote.model.PlacesResponse
import retrofit2.http.*

interface ApiService {

    @POST
    suspend fun buscarCafeteriasNearby(
        @Url url: String,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: NearbySearchRequest
    ): PlacesResponse

    @GET
    suspend fun getDetalleCafeteria(
        @Url url: String,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String = FIELD_MASK_DETAIL
    ): PlaceResult

    companion object {
        const val NEARBY_SEARCH_URL =
            "https://places.googleapis.com/v1/places:searchNearby"
        const val PLACE_DETAIL_BASE_URL =
            "https://places.googleapis.com/v1/places/"

        const val FIELD_MASK_LIST =
            "places.id,places.displayName,places.formattedAddress," +
            "places.location,places.rating,places.userRatingCount,places.photos"

        const val FIELD_MASK_DETAIL =
            "id,displayName,formattedAddress,location,rating," +
            "userRatingCount,photos,regularOpeningHours," +
            "internationalPhoneNumber,websiteUri"
    }
}
