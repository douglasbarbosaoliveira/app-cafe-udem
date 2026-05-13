package com.appcafe.udem.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noticias_eventos")
data class NoticiaEvento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val imagenUrl: String? = null
)
