package com.appcafe.udem.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.databinding.ActivityPerfilBinding
import com.appcafe.udem.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private var usuarioId: Int = -1
    private lateinit var archivoCamara: File

    private val profileViewModel: ProfileViewModel by viewModels {
        val app = application as CafeApplication
        ProfileViewModel.Factory(app.userRepository, app.reviewRepository, app.favoriteRepository, app.visitaRepository)
    }

    // Resultado de la galería
    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            guardarYMostrarFoto(uri)
        }
    }

    // Resultado de la cámara
    private val camaraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = Uri.fromFile(archivoCamara)
            guardarYMostrarFoto(uri)
        }
    }

    // Permiso de cámara
    private val permisoCamaraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
        else Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)
        usuarioId = prefs.getInt("usuario_id", -1)

        if (usuarioId != -1) {
            profileViewModel.setUsuarioId(usuarioId)
            cargarFotoGuardada()
        }

        profileViewModel.usuario.observe(this) { usuario ->
            if (usuario == null && usuarioId != -1) {
                prefs.edit().remove("usuario_id").apply()
                startActivity(Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                return@observe
            }
            usuario ?: return@observe
            binding.nombreUsuario.text = usuario.nombre
            binding.correoUsuario.text = usuario.correo
        }

        profileViewModel.resenas.observe(this) { count ->
            binding.resenasUsuario.text = "$count\nReseñas"
        }
        profileViewModel.favoritos.observe(this) { count ->
            binding.cafesFavoritos.text = "$count\nFavoritos"
        }
        profileViewModel.visitados.observe(this) { count ->
            binding.cafesVisitados.text = "$count\nCafés visitados"
        }

        val descripcionGuardada = prefs.getString("descripcion_$usuarioId", "") ?: ""
        binding.descripcionUsuario.text = descripcionGuardada

        // Click en la foto → elegir fuente
        binding.imagenPerfil.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Foto de perfil")
                .setItems(arrayOf("Tomar foto", "Elegir de galería")) { _, which ->
                    when (which) {
                        0 -> verificarPermisoCamara()
                        1 -> abrirGaleria()
                    }
                }
                .show()
        }

        binding.btnEditDescripcion.setOnClickListener {
            if (binding.inputDescripcion.visibility == View.VISIBLE) cerrarEdicion()
            else abrirEdicion(descripcionGuardada)
        }

        binding.btnGuardarDescripcion.setOnClickListener {
            val nuevaDesc = binding.inputDescripcion.text.toString().trim()
            binding.descripcionUsuario.text = nuevaDesc
            prefs.edit().putString("descripcion_$usuarioId", nuevaDesc).apply()
            cerrarEdicion()
            Toast.makeText(this, "Descripción actualizada", Toast.LENGTH_SHORT).show()
        }

        binding.botonConfiguracion.setOnClickListener {
            startActivity(Intent(this, ConfiguracionActivity::class.java))
        }

        binding.footer.navHome.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        binding.footer.navSearch.setOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }
        binding.footer.navWeb.setOnClickListener { startActivity(Intent(this, ComunidadActivity::class.java)) }
        binding.footer.navFav.setOnClickListener { startActivity(Intent(this, FavoritosActivity::class.java)) }
        binding.footer.navUser.setOnClickListener { }

        binding.botonCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    prefs.edit()
                        .remove("usuario_id")
                        .remove("descripcion_$usuarioId")
                        .apply()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamara()
        } else {
            permisoCamaraLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        archivoCamara = File(cacheDir, "foto_perfil_$usuarioId.jpg")
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            archivoCamara
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        camaraLauncher.launch(intent)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galeriaLauncher.launch(intent)
    }

    private fun guardarYMostrarFoto(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Guardar en archivo interno
            val archivo = File(filesDir, "foto_perfil_$usuarioId.jpg")
            FileOutputStream(archivo).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }

            binding.imagenPerfil.setImageBitmap(bitmap)
            Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarFotoGuardada() {
        val archivo = File(filesDir, "foto_perfil_$usuarioId.jpg")
        if (archivo.exists()) {
            val bitmap = BitmapFactory.decodeFile(archivo.absolutePath)
            binding.imagenPerfil.setImageBitmap(bitmap)
        }
    }

    private fun abrirEdicion(textoActual: String) {
        binding.inputDescripcion.setText(textoActual)
        binding.layoutDescripcion.visibility = View.GONE
        binding.inputDescripcion.visibility = View.VISIBLE
        binding.btnGuardarDescripcion.visibility = View.VISIBLE
        binding.inputDescripcion.requestFocus()
    }

    private fun cerrarEdicion() {
        binding.inputDescripcion.visibility = View.GONE
        binding.btnGuardarDescripcion.visibility = View.GONE
        binding.layoutDescripcion.visibility = View.VISIBLE
    }
}