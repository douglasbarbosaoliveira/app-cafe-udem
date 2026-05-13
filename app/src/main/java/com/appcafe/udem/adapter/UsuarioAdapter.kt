package com.appcafe.udem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.appcafe.udem.R
import com.appcafe.udem.data.local.entities.Usuario
import com.appcafe.udem.databinding.ItemUsuarioBinding

// Adapter para la lista de usuarios. Permite agregar/quitar amigos
// y navegar al perfil público de cada usuario al tocar su nombre.
class UsuarioAdapter(
    private val amigosIds: Set<Int>,
    private val onToggleAmigo: (Usuario, Boolean) -> Unit,
    private val onVerPerfil: (Usuario) -> Unit
) : ListAdapter<Usuario, UsuarioAdapter.ViewHolder>(DIFF) {

    private val estadoAmigos = mutableSetOf<Int>().apply { addAll(amigosIds) }

    inner class ViewHolder(val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Usuario) {
            binding.txtNombre.text = item.nombre
            binding.txtCorreo.text = item.correo

            // Cargar foto del usuario o mostrar ícono por defecto
            if (!item.foto.isNullOrEmpty()) {
                binding.imgUsuario.load(item.foto)
            } else {
                binding.imgUsuario.setImageResource(R.drawable.ic_user)
            }

            val esAmigo = estadoAmigos.contains(item.id)
            actualizarBoton(esAmigo)

            // Al tocar el botón, agregar o quitar de amigos
            binding.btnAgregar.setOnClickListener {
                val ahora = estadoAmigos.contains(item.id)
                if (ahora) estadoAmigos.remove(item.id) else estadoAmigos.add(item.id)
                actualizarBoton(!ahora)
                onToggleAmigo(item, !ahora)
            }

            // Al tocar el item, abrir el perfil del usuario
            binding.root.setOnClickListener {
                onVerPerfil(item)
            }
        }

        private fun actualizarBoton(esAmigo: Boolean) {
            if (esAmigo) {
                binding.btnAgregar.text = "Amigo ✓"
                binding.btnAgregar.setBackgroundColor(
                    binding.root.context.getColor(R.color.GrayButtons)
                )
                binding.btnAgregar.setTextColor(
                    binding.root.context.getColor(R.color.black)
                )
            } else {
                binding.btnAgregar.text = "Agregar"
                binding.btnAgregar.setBackgroundColor(
                    binding.root.context.getColor(R.color.Red)
                )
                binding.btnAgregar.setTextColor(
                    binding.root.context.getColor(R.color.white)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Usuario>() {
            override fun areItemsTheSame(old: Usuario, new: Usuario) = old.id == new.id
            override fun areContentsTheSame(old: Usuario, new: Usuario) = old == new
        }
    }
}