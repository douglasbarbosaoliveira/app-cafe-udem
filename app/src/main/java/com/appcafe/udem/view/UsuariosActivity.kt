package com.appcafe.udem.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.adapter.UsuarioAdapter
import com.appcafe.udem.databinding.ActivityUsuariosBinding
import com.appcafe.udem.viewmodel.UsuariosViewModel

// Muestra la lista de todos los usuarios registrados en la app.
// Permite agregar amigos y navegar al perfil público de cada usuario.
class UsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsuariosBinding

    private val viewModel: UsuariosViewModel by viewModels {
        UsuariosViewModel.Factory((application as CafeApplication).userRepository)
    }

    private var usuarioActualId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)
        usuarioActualId = prefs.getInt("usuario_id", -1)

        // Recuperar lista de amigos guardados en SharedPreferences
        val amigosGuardados = prefs.getStringSet("amigos_$usuarioActualId", emptySet())!!
            .mapNotNull { it.toIntOrNull() }.toSet()

        val adapter = UsuarioAdapter(
            amigosIds = amigosGuardados,
            // Guardar o quitar amigo en SharedPreferences al tocar el botón
            onToggleAmigo = { usuario, esAmigo ->
                val amigos = prefs.getStringSet(
                    "amigos_$usuarioActualId", mutableSetOf()
                )!!.toMutableSet()
                if (esAmigo) amigos.add(usuario.id.toString())
                else amigos.remove(usuario.id.toString())
                prefs.edit().putStringSet("amigos_$usuarioActualId", amigos).apply()
            },
            // Abrir perfil público del usuario al tocar su nombre
            onVerPerfil = { usuario ->
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra(UserProfileActivity.EXTRA_USUARIO_ID, usuario.id)
                startActivity(intent)
            }
        )

        binding.recyclerUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerUsers.adapter = adapter

        // Observar lista de usuarios y mostrar todos menos el usuario actual
        viewModel.usuariosFiltrados.observe(this) { lista ->
            adapter.submitList(lista.filter { it.id != usuarioActualId })
        }

        binding.btnBack.setOnClickListener { finish() }
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
        binding.footer.navFav.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        binding.footer.navUser.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }
}