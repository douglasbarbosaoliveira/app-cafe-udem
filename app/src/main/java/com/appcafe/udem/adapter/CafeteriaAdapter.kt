package com.appcafe.udem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appcafe.udem.data.local.entities.Cafeteria
import com.appcafe.udem.databinding.ItemCafeBinding

class CafeteriaAdapter(
    private val onClick: (Cafeteria) -> Unit
) : ListAdapter<Cafeteria, CafeteriaAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemCafeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cafeteria) {
            binding.txtName.text = item.nombre
            binding.txtDistance.text = item.direccion
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCafeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
