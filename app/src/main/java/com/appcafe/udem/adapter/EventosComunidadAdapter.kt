package com.appcafe.udem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appcafe.udem.databinding.ItemEventoComunidadBinding
import com.appcafe.udem.model.EventoComunidad

class EventosComunidadAdapter(
    private val onToggleUnirse: ((index: Int) -> Unit)? = null
) : ListAdapter<EventoComunidad, EventosComunidadAdapter.EventoViewHolder>(DIFF) {

    inner class EventoViewHolder(val binding: ItemEventoComunidadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(evento: EventoComunidad, position: Int) {
            binding.tituloEvento.text = evento.titulo
            binding.lugarEvento.text = evento.lugar
            binding.fechaEvento.text = evento.fecha
            binding.descripcionEvento.text = evento.descripcion

            if (onToggleUnirse != null) {
                actualizarBoton(evento.joined)
                binding.btnUnirme.setOnClickListener {
                    onToggleUnirse.invoke(position)
                }
            } else {
                binding.btnUnirme.visibility = android.view.View.GONE
            }
        }

        private fun actualizarBoton(joined: Boolean) {
            if (joined) {
                binding.btnUnirme.text = "Ya estoy inscrito — Cancelar"
                binding.btnUnirme.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        binding.root.context.getColor(com.appcafe.udem.R.color.GrayButtons)
                    )
                binding.btnUnirme.setTextColor(
                    binding.root.context.getColor(com.appcafe.udem.R.color.black)
                )
            } else {
                binding.btnUnirme.text = "Unirme al evento"
                binding.btnUnirme.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        binding.root.context.getColor(com.appcafe.udem.R.color.Red)
                    )
                binding.btnUnirme.setTextColor(
                    binding.root.context.getColor(com.appcafe.udem.R.color.white)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val binding = ItemEventoComunidadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<EventoComunidad>() {
            override fun areItemsTheSame(old: EventoComunidad, new: EventoComunidad) =
                old.titulo == new.titulo
            override fun areContentsTheSame(old: EventoComunidad, new: EventoComunidad) =
                old == new
        }
    }
}
