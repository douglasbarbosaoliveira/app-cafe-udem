package com.appcafe.udem.data.repository

import androidx.lifecycle.LiveData
import com.appcafe.udem.data.local.dao.VisitaDao
import com.appcafe.udem.data.local.entities.Visita

class VisitaRepository(private val visitaDao: VisitaDao) {

    suspend fun registrarVisita(usuarioId: Int, cafeteriaId: String): Boolean {
        val rowId = visitaDao.insertar(Visita(usuarioId = usuarioId, cafeteriaId = cafeteriaId))
        return rowId != -1L
    }

    fun contarVisitas(usuarioId: Int): LiveData<Int> = visitaDao.contarVisitas(usuarioId)

    suspend fun estaVisitada(usuarioId: Int, cafeteriaId: String): Boolean =
        visitaDao.estaVisitada(usuarioId, cafeteriaId) > 0
}
