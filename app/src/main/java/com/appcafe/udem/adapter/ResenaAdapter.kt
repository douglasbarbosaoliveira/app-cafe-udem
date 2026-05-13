package com.appcafe.udem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appcafe.udem.data.local.model.ResenaConUsuario
import com.appcafe.udem.databinding.ItemResenaBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResenaAdapter : ListAdapter<ResenaConUsuario, ResenaAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemResenaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResenaConUsuario) {
            binding.txtAutor.text = item.nombreUsuario ?: "Usuario"
            binding.txtRating.text = "★ ${"%.1f".format(item.resena.rating)}"
            binding.txtComentario.text = item.resena.comentario ?: "Sin comentario"
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.txtFecha.text = sdf.format(Date(item.resena.fecha))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemResenaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ResenaConUsuario>() {
            override fun areItemsTheSame(old: ResenaConUsuario, new: ResenaConUsuario) =
                old.resena.id == new.resena.id
            override fun areContentsTheSame(old: ResenaConUsuario, new: ResenaConUsuario) =
                old == new
        }
    }
}
