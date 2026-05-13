package com.appcafe.udem.viewmodel

import androidx.lifecycle.*
import com.appcafe.udem.data.local.entities.NoticiaEvento
import com.appcafe.udem.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    val noticias: LiveData<List<NoticiaEvento>> = newsRepository.getAllNoticias()

    private val _noticiaSeleccionada = MutableLiveData<NoticiaEvento?>()
    val noticiaSeleccionada: LiveData<NoticiaEvento?> = _noticiaSeleccionada

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun seleccionarNoticia(noticia: NoticiaEvento) {
        _noticiaSeleccionada.value = noticia
    }

    fun agregarNoticia(titulo: String, descripcion: String, fecha: Long, imagenUrl: String?) {
        if (titulo.isBlank() || descripcion.isBlank()) {
            _error.value = "Completa título y descripción"
            return
        }
        viewModelScope.launch {
            newsRepository.agregarNoticia(
                NoticiaEvento(
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = fecha,
                    imagenUrl = imagenUrl
                )
            )
            _error.value = null
        }
    }

    fun eliminarNoticia(noticia: NoticiaEvento) {
        viewModelScope.launch {
            newsRepository.eliminarNoticia(noticia)
        }
    }

    class Factory(private val repository: NewsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NewsViewModel(repository) as T
    }
}
