package com.appcafe.udem.data.repository

import androidx.lifecycle.LiveData
import com.appcafe.udem.data.local.dao.CoffeeDao
import com.appcafe.udem.data.local.entities.Cafeteria

class RecommendationRepository(private val coffeeDao: CoffeeDao) {

    // Devuelve cafeterías aleatorias de la base de datos local como recomendaciones
    fun getCafeteriasRecomendadas(limit: Int = 5): LiveData<List<Cafeteria>> =
        coffeeDao.getCafeteriasAleatorias(limit)
}
