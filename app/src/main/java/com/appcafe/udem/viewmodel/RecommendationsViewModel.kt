package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.data.repository.RecommendationRepository

class RecommendationsViewModel(
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {

    // Devuelve cafeterías aleatorias de la base de datos local como recomendaciones
    val cafeteriasRecomendadas: LiveData<List<Cafeteria>> =
        recommendationRepository.getCafeteriasRecomendadas(limit = 5)

    class Factory(
        private val repository: RecommendationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RecommendationsViewModel(repository) as T
    }
}
