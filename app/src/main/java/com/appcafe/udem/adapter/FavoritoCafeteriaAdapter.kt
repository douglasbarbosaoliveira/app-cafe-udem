package com.appcafe.udem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.appcafe.udem.BuildConfig
import com.appcafe.udem.R
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.databinding.ItemFavoriteBinding

class FavoritoCafeteriaAdapter(
    private val onClick: (Cafeteria) -> Unit,
    private val onRemove: (Cafeteria) -> Unit
) : ListAdapter<Cafeteria, FavoritoCafeteriaAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cafeteria) {
            binding.txtCafeName.text = item.nombre
            binding.txtUser.text = item.direccion
            val rawUrl = item.imagenUrl
            val loadUrl = if (!rawUrl.isNullOrEmpty()) {
                if (rawUrl.startsWith("http")) rawUrl
                else "https://places.googleapis.com/v1/$rawUrl/media?key=${BuildConfig.PLACES_API_KEY}&maxHeightPx=400&maxWidthPx=400"
            } else null
            binding.imgCafeteria.load(loadUrl) {
                crossfade(true)
                placeholder(R.drawable.placeholder_cafe)
                error(R.drawable.placeholder_cafe)
            }
            binding.root.setOnClickListener { onClick(item) }
            binding.root.setOnLongClickListener {
                onRemove(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Cafeteria>() {
            override fun areItemsTheSame(old: Cafeteria, new: Cafeteria) = old.id == new.id
            override fun areContentsTheSame(old: Cafeteria, new: Cafeteria) = old == new
        }
    }
}
