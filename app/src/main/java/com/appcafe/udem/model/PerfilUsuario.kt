package com.appcafe.udem.model

data class PerfilUsuario(
    val nombre: String,
    val correo: String,
    val descripcion: String,
    val cafesVisitados: Int,
    val favoritos: Int,
    val resenas: Int
)
