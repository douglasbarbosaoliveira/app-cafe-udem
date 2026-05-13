package com.appcafe.udem.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.adapter.FavoritoCafeteriaAdapter
import com.appcafe.udem.databinding.ActivityFavoritosBinding
import com.appcafe.udem.viewmodel.FavoritesViewModel

class FavoritosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritosBinding

    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModel.Factory((application as CafeApplication).favoriteRepository)
    }

    private val adapter = FavoritoCafeteriaAdapter(
        onClick = { cafeteria ->
            val intent = Intent(this, DetailCafeteriaActivity::class.java)
            intent.putExtra(DetailCafeteriaActivity.EXTRA_CAFETERIA_ID, cafeteria.id)
            startActivity(intent)
        },
        onRemove = { cafeteria ->
            viewModel.eliminarFavorito(cafeteria.id)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(this)
        binding.recyclerFavorites.adapter = adapter

        val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)
        val usuarioId = prefs.getInt("usuario_id", -1)
        if (usuarioId != -1) viewModel.setUsuarioId(usuarioId)

        viewModel.cafeteriasFavoritas.observe(this) { lista ->
            adapter.submitList(lista)
        }

        viewModel.accionExitosa.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.limpiarAccion()
            }
        }

        binding.cardPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        binding.btnSeeAll.setOnClickListener {
            binding.scrollView.post {
                binding.scrollView.smoothScrollTo(0, binding.recyclerFavorites.top)
            }
        }

        wireFooter()
    }

    private fun wireFooter() {
        binding.footer.navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.footer.navSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        binding.footer.navWeb.setOnClickListener {
            startActivity(Intent(this, ComunidadActivity::class.java))
        }
        binding.footer.navFav.setOnClickListener { /* ya estamos aquí */ }
        binding.footer.navUser.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }
}
