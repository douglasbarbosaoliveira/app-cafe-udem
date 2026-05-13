package com.appcafe.udem.data.repository

import androidx.lifecycle.LiveData
import com.appcafe.udem.data.local.dao.UserDao
import com.appcafe.udem.data.local.entities.Usuario

class UserRepository(private val userDao: UserDao) {

    suspend fun registrarUsuario(usuario: Usuario) {
        userDao.insertUsuario(usuario)
    }

    suspend fun actualizarUsuario(usuario: Usuario) {
        userDao.updateUsuario(usuario)
    }

    fun getUsuarioById(id: Int): LiveData<Usuario> =
        userDao.getUsuarioById(id)

    suspend fun getUsuarioByCorreo(correo: String): Usuario? =
        userDao.getUsuarioByCorreo(correo)

    fun getAllUsuarios(): LiveData<List<Usuario>> = userDao.getAllUsuarios()

    suspend fun login(correo: String, contrasena: String): Usuario? {
        val usuario = userDao.getUsuarioByCorreo(correo) ?: return null
        return if (usuario.contrasena == contrasena) usuario else null
    }
}
