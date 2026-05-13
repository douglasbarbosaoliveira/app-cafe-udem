package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.data.local.entities.Resena
import com.appcafe.udem.data.repository.CoffeeRepository
import com.appcafe.udem.data.repository.FavoriteRepository
import com.appcafe.udem.data.repository.ReviewRepository
import com.appcafe.udem.data.repository.VisitaRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val coffeeRepository: CoffeeRepository,
    private val reviewRepository: ReviewRepository,
    private val favoriteRepository: FavoriteRepository,
    private val visitaRepository: VisitaRepository
) : ViewModel() {

    private val _cafeteriaId = MutableLiveData<String>()

    val cafeteria: LiveData<Cafeteria> = _cafeteriaId.switchMap { id ->
        coffeeRepository.getCafeteriaById(id)
    }

    val resenas: LiveData<List<Resena>> = _cafeteriaId.switchMap { id ->
        reviewRepository.getResenasByCafeteria(id)
    }

    val ratingPromedio: LiveData<Float> = _cafeteriaId.switchMap { id ->
        reviewRepository.getRatingPromedio(id)
    }

    private val _esFavorito = MutableLiveData<Boolean>(false)
    val esFavorito: LiveData<Boolean> = _esFavorito

    private val _esVisitada = MutableLiveData<Boolean>(false)
    val esVisitada: LiveData<Boolean> = _esVisitada

    private val _accionExitosa = MutableLiveData<String?>()
    val accionExitosa: LiveData<String?> = _accionExitosa

    fun setCafeteriaId(id: String) {
        _cafeteriaId.value = id
    }

    fun verificarFavorito(usuarioId: Int, cafeteriaId: String) {
        viewModelScope.launch {
            _esFavorito.value = favoriteRepository.esFavorito(usuarioId, cafeteriaId)
        }
    }

    fun toggleFavorito(usuarioId: Int, cafeteriaId: String) {
        viewModelScope.launch {
            if (_esFavorito.value == true) {
                favoriteRepository.eliminarFavorito(usuarioId, cafeteriaId)
                _esFavorito.value = false
                _accionExitosa.value = "Eliminado de favoritos"
            } else {
                favoriteRepository.agregarFavorito(usuarioId, cafeteriaId)
                _esFavorito.value = true
                _accionExitosa.value = "Agregado a favoritos"
            }
        }
    }

    fun verificarVisita(usuarioId: Int, cafeteriaId: String) {
        viewModelScope.launch {
            _esVisitada.value = visitaRepository.estaVisitada(usuarioId, cafeteriaId)
        }
    }

    fun registrarVisita(usuarioId: Int, cafeteriaId: String) {
        viewModelScope.launch {
            val esNueva = visitaRepository.registrarVisita(usuarioId, cafeteriaId)
            if (esNueva) {
                _accionExitosa.value = "¡Cafetería marcada como visitada!"
                _esVisitada.value = true
            } else {
                _accionExitosa.value = "Ya habías registrado esta visita"
            }
        }
    }

    fun limpiarAccion() {
        _accionExitosa.value = null
    }

    class Factory(
        private val coffeeRepository: CoffeeRepository,
        private val reviewRepository: ReviewRepository,
        private val favoriteRepository: FavoriteRepository,
        private val visitaRepository: VisitaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetailViewModel(coffeeRepository, reviewRepository, favoriteRepository, visitaRepository) as T
    }
}
