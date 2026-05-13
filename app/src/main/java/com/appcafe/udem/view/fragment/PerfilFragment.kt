package com.appcafe.udem.view.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.databinding.FragmentPerfilBinding
import com.appcafe.udem.view.ConfiguracionActivity
import com.appcafe.udem.view.LoginActivity
import com.appcafe.udem.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private var usuarioId: Int = -1
    private lateinit var archivoCamara: File

    private val profileViewModel: ProfileViewModel by viewModels {
        val app = requireActivity().application as CafeApplication
        ProfileViewModel.Factory(app.userRepository, app.reviewRepository, app.favoriteRepository, app.visitaRepository)
    }

    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            guardarYMostrarFoto(uri)
        }
    }

    private val camaraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            guardarYMostrarFoto(Uri.fromFile(archivoCamara))
        }
    }

    private val permisoCamaraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
        else Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("cafe_prefs", android.content.Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("usuario_id", -1)

        if (usuarioId != -1) {
            profileViewModel.setUsuarioId(usuarioId)
            cargarFotoGuardada()
        }

        profileViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            if (usuario == null && usuarioId != -1) {
                prefs.edit().remove("usuario_id").apply()
                startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                return@observe
            }
            usuario ?: return@observe
            binding.nombreUsuario.text = usuario.nombre
            binding.correoUsuario.text = usuario.correo
        }

        profileViewModel.resenas.observe(viewLifecycleOwner) { count ->
            binding.resenasUsuario.text = "$count\nReseñas"
        }
        profileViewModel.favoritos.observe(viewLifecycleOwner) { count ->
            binding.cafesFavoritos.text = "$count\nFavoritos"
        }
        profileViewModel.visitados.observe(viewLifecycleOwner) { count ->
            binding.cafesVisitados.text = "$count\nCafés visitados"
        }

        val descripcionGuardada = prefs.getString("descripcion_$usuarioId", "") ?: ""
        binding.descripcionUsuario.text = descripcionGuardada

        binding.imagenPerfil.setOnClickListener {
            AlertDialog.Builder(requireContext())
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
            Toast.makeText(requireContext(), "Descripción actualizada", Toast.LENGTH_SHORT).show()
        }

        binding.botonConfiguracion.setOnClickListener {
            startActivity(Intent(requireContext(), ConfiguracionActivity::class.java))
        }

        binding.botonCerrarSesion.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    prefs.edit().remove("usuario_id").remove("descripcion_$usuarioId").apply()
                    startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) abrirCamara()
        else permisoCamaraLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun abrirCamara() {
        archivoCamara = File(requireContext().cacheDir, "foto_perfil_$usuarioId.jpg")
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", archivoCamara)
        camaraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        })
    }

    private fun abrirGaleria() {
        galeriaLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }

    private fun guardarYMostrarFoto(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            val archivo = File(requireContext().filesDir, "foto_perfil_$usuarioId.jpg")
            FileOutputStream(archivo).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out) }
            binding.imagenPerfil.setImageBitmap(bitmap)
            Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al guardar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarFotoGuardada() {
        val archivo = File(requireContext().filesDir, "foto_perfil_$usuarioId.jpg")
        if (archivo.exists()) binding.imagenPerfil.setImageBitmap(BitmapFactory.decodeFile(archivo.absolutePath))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
