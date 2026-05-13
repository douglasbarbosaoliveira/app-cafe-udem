package com.appcafe.udem.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.adapter.ResenaAdapter
import com.appcafe.udem.databinding.ActivityVerResenasBinding
import com.appcafe.udem.viewmodel.ReviewViewModel

class VerResenasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerResenasBinding

    private val viewModel: ReviewViewModel by viewModels {
        ReviewViewModel.Factory((application as CafeApplication).reviewRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerResenasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cafeteriaId = intent.getStringExtra(EXTRA_CAFETERIA_ID) ?: run { finish(); return }
        val nombre = intent.getStringExtra(EXTRA_CAFETERIA_NOMBRE) ?: ""

        binding.txtTitulo.text = "Reseñas: $nombre"
        binding.btnBack.setOnClickListener { finish() }

        val adapter = ResenaAdapter()
        binding.recyclerResenas.layoutManager = LinearLayoutManager(this)
        binding.recyclerResenas.adapter = adapter

        viewModel.getResenasByCafeteriaConNombre(cafeteriaId).observe(this) { resenas ->
            adapter.submitList(resenas)
            binding.txtSinResenas.visibility = if (resenas.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    companion object {
        const val EXTRA_CAFETERIA_ID = "cafeteria_id"
        const val EXTRA_CAFETERIA_NOMBRE = "cafeteria_nombre"
    }
}
