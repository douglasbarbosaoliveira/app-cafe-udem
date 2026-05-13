package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.Usuario
import com.appcafe.udem.data.repository.FavoriteRepository
import com.appcafe.udem.data.repository.ReviewRepository
import com.appcafe.udem.data.repository.UserRepository
import com.appcafe.udem.data.repository.VisitaRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val favoriteRepository: FavoriteRepository,
    private val visitaRepository: VisitaRepository
) : ViewModel() {

    private val _usuarioId = MutableLiveData<Int>()

    val usuario: LiveData<Usuario> = _usuarioId.switchMap { id ->
        userRepository.getUsuarioById(id)
    }

    val resenas: LiveData<Int> = _usuarioId.switchMap { id ->
        reviewRepository.getResenasByUsuario(id).map { it.size }
    }

    val favoritos: LiveData<Int> = _usuarioId.switchMap { id ->
        favoriteRepository.getCafeteriasFavoritas(id).map { it.size }
    }

    val visitados: LiveData<Int> = _usuarioId.switchMap { id ->
        visitaRepository.contarVisitas(id)
    }

    private val _actualizacionExitosa = MutableLiveData<Boolean>()
    val actualizacionExitosa: LiveData<Boolean> = _actualizacionExitosa

    fun setUsuarioId(id: Int) {
        _usuarioId.value = id
    }

    fun actualizarPerfil(nombre: String, foto: String?) {
        val id = _usuarioId.value ?: return
        val actual = usuario.value ?: return
        viewModelScope.launch {
            userRepository.actualizarUsuario(
                actual.copy(nombre = nombre, foto = foto)
            )
            _actualizacionExitosa.value = true
        }
    }

    class Factory(
        private val userRepository: UserRepository,
        private val reviewRepository: ReviewRepository,
        private val favoriteRepository: FavoriteRepository,
        private val visitaRepository: VisitaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(userRepository, reviewRepository, favoriteRepository, visitaRepository) as T
    }
}
