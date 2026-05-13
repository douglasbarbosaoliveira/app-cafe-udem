package com.appcafe.udem.data.repository

import androidx.lifecycle.LiveData
import com.appcafe.udem.data.local.dao.FavoriteDao
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.data.local.entities.Favorito

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    fun getCafeteriasFavoritas(usuarioId: Int): LiveData<List<Cafeteria>> =
        favoriteDao.getCafeteriasFavoritasByUsuario(usuarioId)

    suspend fun agregarFavorito(usuarioId: Int, cafeteriaId: String) {
        favoriteDao.insertFavorito(
            Favorito(usuarioId = usuarioId, cafeteriaId = cafeteriaId)
        )
    }

    suspend fun eliminarFavorito(usuarioId: Int, cafeteriaId: String) {
        favoriteDao.deleteFavoritoByIds(usuarioId, cafeteriaId)
    }

    suspend fun esFavorito(usuarioId: Int, cafeteriaId: String): Boolean =
        favoriteDao.getFavorito(usuarioId, cafeteriaId) != null
}
