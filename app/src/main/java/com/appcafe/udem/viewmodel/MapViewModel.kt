package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.data.repository.CoffeeRepository
import kotlinx.coroutines.launch

class MapViewModel(private val coffeeRepository: CoffeeRepository) : ViewModel() {

    val cafeterias: LiveData<List<Cafeteria>> = coffeeRepository.getAllCafeterias()

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarCafeteriasNearby(latitud: Double, longitud: Double, apiKey: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                coffeeRepository.buscarYGuardarCafeteriasNearby(latitud, longitud, apiKey = apiKey)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "No se pudo cargar el mapa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    class Factory(private val repository: CoffeeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MapViewModel(repository) as T
    }
}
