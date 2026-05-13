package com.appcafe.udem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appcafe.udem.model.EventoComunidad

class ComunidadViewModel : ViewModel() {

    private val _eventos = MutableLiveData<List<EventoComunidad>>()
    val eventos: LiveData<List<EventoComunidad>> = _eventos

    init {
        _eventos.value = listOf(
            EventoComunidad(
                titulo = "Coffee meet-up",
                lugar = "Café Limón, Monterrey",
                fecha = "Sábado 18 de mayo · 5:00 PM",
                descripcion = "Únete a la siguiente juntada de la comunidad para conocer nuevas cafeterías y personas con el mismo gusto por el café."
            ),
            EventoComunidad(
                titulo = "Ruta de cafeterías",
                lugar = "Barrio Antiguo, Monterrey",
                fecha = "Viernes 24 de mayo · 6:30 PM",
                descripcion = "Recorrido a pie por las mejores cafeterías del centro histórico. Incluye cata de espressos."
            ),
            EventoComunidad(
                titulo = "Study coffee session",
                lugar = "Café La Nacional",
                fecha = "Miércoles 29 de mayo · 4:00 PM",
                descripcion = "Sesión para estudiar, trabajar y convivir en un ambiente tranquilo con buen café."
            ),
            EventoComunidad(
                titulo = "Taller de latte art",
                lugar = "Café Pergola, San Pedro",
                fecha = "Sábado 1 de junio · 10:00 AM",
                descripcion = "Aprende las técnicas básicas de latte art con baristas profesionales. Cupo limitado."
            ),
            EventoComunidad(
                titulo = "Cata de cafés de origen",
                lugar = "Café Origen, UDEM",
                fecha = "Jueves 6 de junio · 5:00 PM",
                descripcion = "Descubre las diferencias entre cafés de Colombia, Etiopía y México en esta cata guiada."
            )
        )
    }

    fun toggleUnirse(index: Int) {
        val lista = _eventos.value?.toMutableList() ?: return
        if (index < 0 || index >= lista.size) return
        lista[index] = lista[index].copy(joined = !lista[index].joined)
        _eventos.value = lista
    }
}
