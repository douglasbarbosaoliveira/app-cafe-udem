package com.appcafe.udem.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.R
import com.appcafe.udem.databinding.ActivityUserProfileBinding
import com.appcafe.udem.viewmodel.ProfileViewModel

// Muestra el perfil público de otro usuario: nombre, correo,
// cantidad de cafeterías visitadas, favoritos y reseñas.
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    private val viewModel: ProfileViewModel by viewModels {
        val app = application as CafeApplication
        ProfileViewModel.Factory(
            app.userRepository,
            app.reviewRepository,
            app.favoriteRepository,
            app.visitaRepository
        )
    }

    companion object {
        // Clave para pasar el id del usuario por Intent
        const val EXTRA_USUARIO_ID = "extra_usuario_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // Definir ícono padrão mientras no carga la foto
        binding.header.imgProfile.setImageResource(R.drawable.ic_user)

        // Obtener el id del usuario recibido por Intent
        val usuarioId = intent.getIntExtra(EXTRA_USUARIO_ID, -1)
        if (usuarioId == -1) { finish(); return }

        // Cargar los datos del usuario en el ViewModel
        viewModel.setUsuarioId(usuarioId)

        // Observar datos del usuario y actualizar la UI
        viewModel.usuario.observe(this) { usuario ->
            usuario ?: return@observe
            binding.header.txtTitle.text = usuario.nombre
            binding.txtNombre.text = usuario.nombre
            binding.txtCorreo.text = usuario.correo

            // Cargar foto de perfil si existe, si no mantener el ícono padrão
            if (!usuario.foto.isNullOrEmpty()) {
                binding.header.imgProfile.load(usuario.foto)
            }
        }

        // Observar cantidad de cafeterías visitadas
        viewModel.visitados.observe(this) { count ->
            binding.txtVisitados.text = count.toString()
        }

        // Observar cantidad de favoritos
        viewModel.favoritos.observe(this) { count ->
            binding.txtFavoritos.text = count.toString()
        }

        // Observar cantidad de reseñas
        viewModel.resenas.observe(this) { count ->
            binding.txtResenas.text = count.toString()
        }

        // Botón de regresar
        binding.header.btnBack.setOnClickListener { finish() }

        // Botón de enviar mensaje (se conectará con ChatActivity)
        binding.btnEnviarMensaje.setOnClickListener {
            // TODO: abrir ChatActivity con este usuario
        }
    }
}