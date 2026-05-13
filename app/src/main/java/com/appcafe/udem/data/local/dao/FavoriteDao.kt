package com.appcafe.udem.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.data.local.entities.Favorito

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorito(favorito: Favorito)

    @Delete
    suspend fun deleteFavorito(favorito: Favorito)

    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND cafeteriaId = :cafeteriaId")
    suspend fun deleteFavoritoByIds(usuarioId: Int, cafeteriaId: String)

    @Query("""
        SELECT c.* FROM cafeterias c
        INNER JOIN favoritos f ON c.id = f.cafeteriaId
        WHERE f.usuarioId = :usuarioId
        ORDER BY f.fechaAgregado DESC
    """)
    fun getCafeteriasFavoritasByUsuario(usuarioId: Int): LiveData<List<Cafeteria>>

    @Query("SELECT * FROM favoritos WHERE usuarioId = :usuarioId AND cafeteriaId = :cafeteriaId LIMIT 1")
    suspend fun getFavorito(usuarioId: Int, cafeteriaId: String): Favorito?
}
