package com.appcafe.udem.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cafeterias")
data class Cafeteria(
    @PrimaryKey val id: String,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val imagenUrl: String? = null,
    val descripcion: String? = null,
    val rating: Float? = null
)
