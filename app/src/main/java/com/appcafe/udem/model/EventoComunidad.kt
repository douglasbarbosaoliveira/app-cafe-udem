package com.appcafe.udem.model

data class EventoComunidad(
    val titulo: String,
    val lugar: String,
    val fecha: String,
    val descripcion: String,
    val joined: Boolean = false
)
