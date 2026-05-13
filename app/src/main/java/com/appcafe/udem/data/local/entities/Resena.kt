package com.appcafe.udem.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resenas",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Cafeteria::class,
            parentColumns = ["id"],
            childColumns = ["cafeteriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId"), Index("cafeteriaId")]
)
data class Resena(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val cafeteriaId: String,
    val rating: Float,
    val comentario: String? = null,
    val fecha: Long = System.currentTimeMillis()
)
