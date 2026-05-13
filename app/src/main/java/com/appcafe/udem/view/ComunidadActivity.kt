package com.appcafe.udem.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcafe.udem.adapter.EventosComunidadAdapter
import com.appcafe.udem.databinding.ActivityComunidadBinding
import com.appcafe.udem.viewmodel.ComunidadViewModel

class ComunidadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComunidadBinding
    private val viewModel: ComunidadViewModel by viewModels()

    private val adapter = EventosComunidadAdapter(
        onToggleUnirse = { index -> viewModel.toggleUnirse(index) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComunidadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerEventos.layoutManager = LinearLayoutManager(this)
        binding.recyclerEventos.adapter = adapter

        viewModel.eventos.observe(this) { lista ->
            adapter.submitList(lista)
        }

        binding.footer.navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.footer.navSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        binding.footer.navWeb.setOnClickListener { /* ya estamos aquí */ }
        binding.footer.navFav.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        binding.footer.navUser.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }
}
