package com.appcafe.udem.data.repository

import androidx.lifecycle.LiveData
import com.appcafe.udem.data.local.dao.CoffeeDao
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.data.remote.api.ApiService
import com.appcafe.udem.data.remote.model.Circle
import com.appcafe.udem.data.remote.model.LatLngRequest
import com.appcafe.udem.data.remote.model.LocationRestriction
import com.appcafe.udem.data.remote.model.NearbySearchRequest

class CoffeeRepository(
    private val coffeeDao: CoffeeDao,
    private val apiService: ApiService
) {

    fun getAllCafeterias(): LiveData<List<Cafeteria>> =
        coffeeDao.getAllCafeterias()

    fun getCafeteriaById(id: String): LiveData<Cafeteria> =
        coffeeDao.getCafeteriaById(id)

    suspend fun buscarYGuardarCafeteriasNearby(
        latitud: Double,
        longitud: Double,
        radioMetros: Double = 1500.0,
        apiKey: String
    ) {
        val request = NearbySearchRequest(
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLngRequest(latitud, longitud),
                    radius = radioMetros
                )
            )
        )
        val response = apiService.buscarCafeteriasNearby(
            url = ApiService.NEARBY_SEARCH_URL,
            apiKey = apiKey,
            fieldMask = ApiService.FIELD_MASK_LIST,
            request = request
        )
        val cafeterias = response.places?.map { place ->
            val photoUrl = place.photos?.firstOrNull()?.name?.let { photoName ->
                "https://places.googleapis.com/v1/$photoName/media?key=$apiKey&maxHeightPx=400&maxWidthPx=400"
            }
            Cafeteria(
                id = place.id,
                nombre = place.displayName?.text ?: "",
                direccion = place.formattedAddress ?: "",
                latitud = place.location?.latitude ?: 0.0,
                longitud = place.location?.longitude ?: 0.0,
                imagenUrl = photoUrl,
                rating = place.rating?.toFloat()
            )
        } ?: emptyList()
        coffeeDao.insertAll(cafeterias)
    }
}
