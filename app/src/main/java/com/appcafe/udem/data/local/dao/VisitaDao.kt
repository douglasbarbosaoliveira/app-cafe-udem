package com.appcafe.udem.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appcafe.udem.data.local.entities.Visita

@Dao
interface VisitaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(visita: Visita): Long

    @Query("SELECT COUNT(*) FROM visitas WHERE usuarioId = :usuarioId")
    fun contarVisitas(usuarioId: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM visitas WHERE usuarioId = :usuarioId AND cafeteriaId = :cafeteriaId")
    suspend fun estaVisitada(usuarioId: Int, cafeteriaId: String): Int
}
