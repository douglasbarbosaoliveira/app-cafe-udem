package com.appcafe.udem.data.repository

import androidx.lifecycle.LiveData
import com.appcafe.udem.data.local.dao.ReviewDao
import com.appcafe.udem.data.local.entities.Resena
import com.appcafe.udem.data.local.model.ResenaConUsuario

class ReviewRepository(private val reviewDao: ReviewDao) {

    fun getResenasByCafeteria(cafeteriaId: String): LiveData<List<Resena>> =
        reviewDao.getResenasByCafeteria(cafeteriaId)

    fun getResenasByCafeteriaConNombre(cafeteriaId: String): LiveData<List<ResenaConUsuario>> =
        reviewDao.getResenasByCafeteriaConNombre(cafeteriaId)

    fun getResenasByUsuario(usuarioId: Int): LiveData<List<Resena>> =
        reviewDao.getResenasByUsuario(usuarioId)

    fun getRatingPromedio(cafeteriaId: String): LiveData<Float> =
        reviewDao.getRatingPromedioByCafeteria(cafeteriaId)

    suspend fun agregarResena(resena: Resena) {
        reviewDao.insertResena(resena)
    }

    suspend fun eliminarResena(resena: Resena) {
        reviewDao.deleteResena(resena)
    }
}
