package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoriteRepository: FavoriteRepository) : ViewModel() {

    private val _usuarioId = MutableLiveData<Int>()

    val cafeteriasFavoritas: LiveData<List<Cafeteria>> = _usuarioId.switchMap { id ->
        favoriteRepository.getCafeteriasFavoritas(id)
    }

    private val _accionExitosa = MutableLiveData<String?>()
    val accionExitosa: LiveData<String?> = _accionExitosa

    fun setUsuarioId(id: Int) {
        _usuarioId.value = id
    }

    fun eliminarFavorito(cafeteriaId: String) {
        val usuarioId = _usuarioId.value ?: return
        viewModelScope.launch {
            favoriteRepository.eliminarFavorito(usuarioId, cafeteriaId)
            _accionExitosa.value = "Cafetería eliminada de favoritos"
        }
    }

    fun limpiarAccion() {
        _accionExitosa.value = null
    }

    class Factory(private val repository: FavoriteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FavoritesViewModel(repository) as T
    }
}
