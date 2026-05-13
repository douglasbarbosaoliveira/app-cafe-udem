package com.appcafe.udem.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appcafe.udem.data.local.entities.Cafeteria

@Dao
interface CoffeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCafeteria(cafeteria: Cafeteria)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cafeterias: List<Cafeteria>)

    @Delete
    suspend fun deleteCafeteria(cafeteria: Cafeteria)

    @Query("SELECT * FROM cafeterias")
    fun getAllCafeterias(): LiveData<List<Cafeteria>>

    @Query("SELECT * FROM cafeterias WHERE id = :id")
    fun getCafeteriaById(id: String): LiveData<Cafeteria>

    @Query("SELECT * FROM cafeterias ORDER BY RANDOM() LIMIT :limit")
    fun getCafeteriasAleatorias(limit: Int = 5): LiveData<List<Cafeteria>>
}
