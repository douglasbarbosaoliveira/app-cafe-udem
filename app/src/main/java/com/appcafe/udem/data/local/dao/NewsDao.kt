package com.appcafe.udem.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appcafe.udem.data.local.entities.NoticiaEvento

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoticia(noticiaEvento: NoticiaEvento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(noticias: List<NoticiaEvento>)

    @Delete
    suspend fun deleteNoticia(noticiaEvento: NoticiaEvento)

    @Query("SELECT * FROM noticias_eventos ORDER BY fecha DESC")
    fun getAllNoticias(): LiveData<List<NoticiaEvento>>

    @Query("SELECT * FROM noticias_eventos WHERE id = :id")
    fun getNoticiaById(id: Int): LiveData<NoticiaEvento>
}
