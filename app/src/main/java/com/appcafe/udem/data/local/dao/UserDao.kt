package com.appcafe.udem.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appcafe.udem.data.local.entities.Usuario

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario)

    @Update
    suspend fun updateUsuario(usuario: Usuario)

    @Delete
    suspend fun deleteUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioById(id: Int): LiveData<Usuario>

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getUsuarioByCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getAllUsuarios(): LiveData<List<Usuario>>
}
