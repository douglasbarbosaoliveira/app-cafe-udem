package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.Usuario
import com.appcafe.udem.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _usuarioActual = MutableLiveData<Usuario?>()
    val usuarioActual: LiveData<Usuario?> = _usuarioActual

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso: LiveData<Boolean> = _registroExitoso

    fun login(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _error.value = "Ingresa tu correo y contraseña"
            return
        }
        viewModelScope.launch {
            val usuario = userRepository.login(correo, contrasena)
            if (usuario != null) {
                _usuarioActual.value = usuario
                _error.value = null
            } else {
                _error.value = "Correo o contraseña incorrectos"
            }
        }
    }

    fun registrar(nombre: String, correo: String, contrasena: String) {
        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _error.value = "Completa todos los campos"
            return
        }
        viewModelScope.launch {
            val existente = userRepository.getUsuarioByCorreo(correo)
            if (existente != null) {
                _error.value = "El correo ya está registrado"
            } else {
                userRepository.registrarUsuario(
                    Usuario(nombre = nombre, correo = correo, contrasena = contrasena)
                )
                _registroExitoso.value = true
                _error.value = null
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AuthViewModel(repository) as T
    }
}
