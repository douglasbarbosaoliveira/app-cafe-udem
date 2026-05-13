package com.appcafe.udem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appcafe.udem.model.PerfilUsuario

class PerfilViewModel : ViewModel() {

    private val _perfilUsuario = MutableLiveData<PerfilUsuario>()
    val perfilUsuario: LiveData<PerfilUsuario> = _perfilUsuario

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        _perfilUsuario.value = PerfilUsuario(
            nombre = "Raquel de la Garza",
            correo = "raquel@email.com",
            descripcion = "Amante del café y de descubrir nuevas cafeterías en Monterrey.",
            cafesVisitados = 12,
            favoritos = 5,
            resenas = 8
        )
    }
}
