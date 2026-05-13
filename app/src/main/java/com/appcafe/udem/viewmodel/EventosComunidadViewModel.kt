package com.appcafe.udem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appcafe.udem.model.EventoComunidad

class EventosComunidadViewModel : ViewModel() {

    private val _listaEventos = MutableLiveData<List<EventoComunidad>>()
    val listaEventos: LiveData<List<EventoComunidad>> = _listaEventos

    init {
        cargarEventos()
    }

    private fun cargarEventos() {
        _listaEventos.value = listOf(
            EventoComunidad(
                titulo = "Coffee meet-up",
                lugar = "Café Limón, Monterrey",
                fecha = "Sábado 18 de mayo · 5:00 PM",
                descripcion = "Juntada para conocer nuevas cafeterías y personas."
            ),
            EventoComunidad(
                titulo = "Ruta de cafeterías",
                lugar = "Barrio Antiguo",
                fecha = "Viernes 24 de mayo · 6:30 PM",
                descripcion = "Recorrido por diferentes cafeterías del centro."
            ),
            EventoComunidad(
                titulo = "Study coffee session",
                lugar = "Café La Nacional",
                fecha = "Miércoles 29 de mayo · 4:00 PM",
                descripcion = "Sesión para estudiar, trabajar y convivir."
            )
        )
    }
}
