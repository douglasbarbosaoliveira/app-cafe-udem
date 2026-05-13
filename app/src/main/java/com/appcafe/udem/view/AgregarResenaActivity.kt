package com.appcafe.udem.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.databinding.ActivityAgregarResenaBinding
import com.appcafe.udem.viewmodel.ReviewViewModel

class AgregarResenaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarResenaBinding

    private val viewModel: ReviewViewModel by viewModels {
        ReviewViewModel.Factory((application as CafeApplication).reviewRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarResenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cafeteriaId = intent.getStringExtra(EXTRA_CAFETERIA_ID) ?: run { finish(); return }
        val nombre = intent.getStringExtra(EXTRA_CAFETERIA_NOMBRE) ?: ""
        val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)
        val usuarioId = prefs.getInt("usuario_id", -1)

        binding.txtTitulo.text = "Reseña: $nombre"
        binding.btnCancelar.setOnClickListener { finish() }

        binding.btnGuardar.setOnClickListener {
            if (usuarioId == -1) {
                Toast.makeText(this, "Inicia sesión para agregar una reseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val rating = binding.ratingBar.rating
            if (rating == 0f) {
                Toast.makeText(this, "Selecciona una calificación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val comentario = binding.inputComentario.text.toString().trim()
            viewModel.agregarResena(usuarioId, cafeteriaId, rating, comentario)
        }

        viewModel.guardadoExitoso.observe(this) { exito ->
            if (exito == true) {
                Toast.makeText(this, "¡Reseña guardada!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_CAFETERIA_ID = "cafeteria_id"
        const val EXTRA_CAFETERIA_NOMBRE = "cafeteria_nombre"
    }
}
