package com.appcafe.udem.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.databinding.ActivityDetailCafeteriaBinding
import com.appcafe.udem.viewmodel.DetailViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailCafeteriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCafeteriaBinding
    private var googleMap: GoogleMap? = null

    private val viewModel: DetailViewModel by viewModels {
        val app = application as CafeApplication
        DetailViewModel.Factory(app.coffeeRepository, app.reviewRepository, app.favoriteRepository, app.visitaRepository)
    }

    private var usuarioId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCafeteriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cafeteriaId = intent.getStringExtra(EXTRA_CAFETERIA_ID) ?: run { finish(); return }
        val prefs = getSharedPreferences("cafe_prefs", MODE_PRIVATE)
        usuarioId = prefs.getInt("usuario_id", -1)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { map ->
            googleMap = map
            map.uiSettings.setAllGesturesEnabled(false)
            map.uiSettings.isZoomControlsEnabled = false
            map.uiSettings.isMapToolbarEnabled = false
        }

        viewModel.setCafeteriaId(cafeteriaId)
        if (usuarioId != -1) viewModel.verificarFavorito(usuarioId, cafeteriaId)
        if (usuarioId != -1) viewModel.verificarVisita(usuarioId, cafeteriaId)

        viewModel.cafeteria.observe(this) { cafeteria ->
            cafeteria ?: return@observe
            binding.txtName.text = cafeteria.nombre
            binding.txtLocation.text = cafeteria.direccion
            binding.txtAddress.text = cafeteria.direccion
            binding.txtDescription.text = cafeteria.descripcion ?: ""
            binding.txtRating.text = cafeteria.rating?.toString() ?: "—"

            val latLng = LatLng(cafeteria.latitud, cafeteria.longitud)
            googleMap?.let { map ->
                map.clear()
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                map.addMarker(MarkerOptions().position(latLng).title(cafeteria.nombre))
            } ?: binding.mapView.getMapAsync { map ->
                googleMap = map
                map.uiSettings.setAllGesturesEnabled(false)
                map.uiSettings.isZoomControlsEnabled = false
                map.uiSettings.isMapToolbarEnabled = false
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                map.addMarker(MarkerOptions().position(latLng).title(cafeteria.nombre))
            }
        }

        viewModel.esFavorito.observe(this) { esFav ->
            val iconRes = if (esFav) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
            binding.btnHeaderFavorite.setImageResource(iconRes)
            binding.btnFav.setImageResource(iconRes)
        }

        viewModel.esVisitada.observe(this) { visitada ->
            binding.txtFuiste.text = if (visitada) "¡Fuiste!" else "¿Fuiste?"
        }

        viewModel.accionExitosa.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.limpiarAccion()
            }
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnHeaderFavorite.setOnClickListener {
            if (usuarioId == -1) return@setOnClickListener
            viewModel.toggleFavorito(usuarioId, cafeteriaId)
        }

        binding.btnFav.setOnClickListener {
            if (usuarioId == -1) return@setOnClickListener
            viewModel.toggleFavorito(usuarioId, cafeteriaId)
        }

        binding.btnLocation.setOnClickListener {
            viewModel.cafeteria.value?.let { c ->
                val uri = Uri.parse("geo:${c.latitud},${c.longitud}?q=${Uri.encode(c.nombre)}")
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }

        binding.btnGo.setOnClickListener {
            viewModel.cafeteria.value?.let { c ->
                val uri = Uri.parse("google.navigation:q=${c.latitud},${c.longitud}")
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }

        binding.btnReview.setOnClickListener {
            val intent = Intent(this, AgregarResenaActivity::class.java)
            intent.putExtra(AgregarResenaActivity.EXTRA_CAFETERIA_ID, cafeteriaId)
            intent.putExtra(AgregarResenaActivity.EXTRA_CAFETERIA_NOMBRE, binding.txtName.text.toString())
            startActivity(intent)
        }

        binding.btnReviews.setOnClickListener {
            val intent = Intent(this, VerResenasActivity::class.java)
            intent.putExtra(VerResenasActivity.EXTRA_CAFETERIA_ID, cafeteriaId)
            intent.putExtra(VerResenasActivity.EXTRA_CAFETERIA_NOMBRE, binding.txtName.text.toString())
            startActivity(intent)
        }

        binding.btnMenu.setOnClickListener {
            Toast.makeText(this, "Menú próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.btnVisited.setOnClickListener {
            if (usuarioId == -1) return@setOnClickListener
            viewModel.registrarVisita(usuarioId, cafeteriaId)
        }

        wireFooter()
    }

    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onDestroy() { super.onDestroy(); binding.mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
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

    companion object {
        const val EXTRA_CAFETERIA_ID = "cafeteria_id"
    }
}
