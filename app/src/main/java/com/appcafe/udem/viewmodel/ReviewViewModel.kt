package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.Resena
import com.appcafe.udem.data.local.model.ResenaConUsuario
import com.appcafe.udem.data.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {

    private val _cafeteriaId = MutableLiveData<String>()

    val resenas: LiveData<List<Resena>> = _cafeteriaId.switchMap { id ->
        reviewRepository.getResenasByCafeteria(id)
    }

    val ratingPromedio: LiveData<Float> = _cafeteriaId.switchMap { id ->
        reviewRepository.getRatingPromedio(id)
    }

    private val _resenaGuardada = MutableLiveData<Boolean>()
    val resenaGuardada: LiveData<Boolean> = _resenaGuardada

    private val _guardadoExitoso = MutableLiveData<Boolean>()
    val guardadoExitoso: LiveData<Boolean> = _guardadoExitoso

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun setCafeteriaId(id: String) {
        _cafeteriaId.value = id
    }

    fun agregarResena(usuarioId: Int, cafeteriaId: String, rating: Float, comentario: String?) {
        if (rating <= 0f) {
            _error.value = "Selecciona una calificación"
            return
        }
        viewModelScope.launch {
            reviewRepository.agregarResena(
                Resena(
                    usuarioId = usuarioId,
                    cafeteriaId = cafeteriaId,
                    rating = rating,
                    comentario = comentario?.takeIf { it.isNotBlank() }
                )
            )
            _resenaGuardada.value = true
            _guardadoExitoso.value = true
            _error.value = null
        }
    }

    fun getResenasByCafeteria(cafeteriaId: String): LiveData<List<Resena>> =
        reviewRepository.getResenasByCafeteria(cafeteriaId)

    fun getResenasByCafeteriaConNombre(cafeteriaId: String): LiveData<List<ResenaConUsuario>> =
        reviewRepository.getResenasByCafeteriaConNombre(cafeteriaId)

    fun limpiarEstado() {
        _resenaGuardada.value = false
        _error.value = null
    }

    class Factory(private val repository: ReviewRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReviewViewModel(repository) as T
    }
}
