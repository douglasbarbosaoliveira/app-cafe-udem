package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.Usuario
import com.appcafe.udem.data.repository.UserRepository

class UsuariosViewModel(private val userRepository: UserRepository) : ViewModel() {

    val usuarios: LiveData<List<Usuario>> = userRepository.getAllUsuarios()

    private val _query = MutableLiveData<String>("")

    val usuariosFiltrados: LiveData<List<Usuario>> = MediatorLiveData<List<Usuario>>().apply {
        fun update() {
            val lista = usuarios.value ?: emptyList()
            val q = _query.value ?: ""
            value = if (q.isBlank()) lista
            else lista.filter { it.nombre.contains(q, ignoreCase = true) || it.correo.contains(q, ignoreCase = true) }
        }
        addSource(usuarios) { update() }
        addSource(_query) { update() }
    }

    fun buscar(query: String) {
        _query.value = query
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            UsuariosViewModel(repository) as T
    }
}
