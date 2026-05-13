package com.appcafe.udem.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.appcafe.udem.data.local.entities.Resena

data class ResenaConUsuario(
    @Embedded val resena: Resena,
    @ColumnInfo(name = "nombreUsuario") val nombreUsuario: String?
)
