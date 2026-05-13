package com.appcafe.udem.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appcafe.udem.data.local.entities.Resena
import com.appcafe.udem.data.local.model.ResenaConUsuario

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResena(resena: Resena)

    @Delete
    suspend fun deleteResena(resena: Resena)

    @Query("SELECT * FROM resenas WHERE cafeteriaId = :cafeteriaId ORDER BY fecha DESC")
    fun getResenasByCafeteria(cafeteriaId: String): LiveData<List<Resena>>

    @Query("""
        SELECT r.*, u.nombre AS nombreUsuario
        FROM resenas r
        LEFT JOIN usuarios u ON r.usuarioId = u.id
        WHERE r.cafeteriaId = :cafeteriaId
        ORDER BY r.fecha DESC
    """)
    fun getResenasByCafeteriaConNombre(cafeteriaId: String): LiveData<List<ResenaConUsuario>>

    @Query("SELECT * FROM resenas WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun getResenasByUsuario(usuarioId: Int): LiveData<List<Resena>>

    @Query("SELECT AVG(rating) FROM resenas WHERE cafeteriaId = :cafeteriaId")
    fun getRatingPromedioByCafeteria(cafeteriaId: String): LiveData<Float>
}
