package com.appcafe.udem.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "visitas",
    indices = [Index(value = ["usuarioId", "cafeteriaId"], unique = true)]
)
data class Visita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val cafeteriaId: String,
    val fecha: Long = System.currentTimeMillis()
)
