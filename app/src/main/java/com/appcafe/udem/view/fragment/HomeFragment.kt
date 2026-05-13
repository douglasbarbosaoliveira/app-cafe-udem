package com.appcafe.udem.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.appcafe.udem.BuildConfig
import com.appcafe.udem.CafeApplication
import com.appcafe.udem.R
import com.appcafe.udem.databinding.FragmentHomeBinding
import com.appcafe.udem.view.AccesibilidadActivity
import com.appcafe.udem.view.ConfiguracionActivity
import com.appcafe.udem.view.MainContainerActivity
import com.appcafe.udem.view.DetailCafeteriaActivity
import com.appcafe.udem.view.UsuariosActivity
import com.appcafe.udem.viewmodel.CoffeeViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null

    // Sensor de luz ambiental
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    // Evita que la sugerencia de Dark Mode aparezca más de una vez por sesión
    private var darkModeSuggested = false

    // Umbral de luz en lux: por debajo de este valor se considera ambiente oscuro
    private val LIGHT_THRESHOLD = 3f

    private val coffeeViewModel: CoffeeViewModel by viewModels {
        CoffeeViewModel.Factory((requireActivity().application as CafeApplication).coffeeRepository)
    }

    private val UDEM_LAT = 25.6499
    private val UDEM_LNG = -100.4058
    private val CHANNEL_ID = "canal_eventos_cafe"

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) obtenerUbicacionYBuscar()
        else buscarCafeterias(UDEM_LAT, UDEM_LNG)
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> if (concedido) mostrarNotificacionBienvenida() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el gestor de sensores y obtener el sensor de luz
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        solicitarPermisoYNotificar()

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { map ->
            googleMap = map
            map.uiSettings.setAllGesturesEnabled(false)
            map.uiSettings.isZoomControlsEnabled = false
            map.uiSettings.isMapToolbarEnabled = false
        }

        pedirUbicacionYBuscarCafeterias()

        coffeeViewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty())
                Toast.makeText(requireContext(), "Error al cargar cafeterías: $msg", Toast.LENGTH_SHORT).show()
        }

        coffeeViewModel.cafeterias.observe(viewLifecycleOwner) { cafeterias ->
            if (cafeterias.isNullOrEmpty()) return@observe
            val center = cafeterias.first()
            val centerLatLng = LatLng(center.latitud, center.longitud)
            googleMap?.let { map ->
                map.clear()
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14f))
                cafeterias.forEach { c ->
                    map.addMarker(MarkerOptions().position(LatLng(c.latitud, c.longitud)).title(c.nombre))
                }
            } ?: binding.mapView.getMapAsync { map ->
                googleMap = map
                map.uiSettings.setAllGesturesEnabled(false)
                map.uiSettings.isZoomControlsEnabled = false
                map.uiSettings.isMapToolbarEnabled = false
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14f))
                cafeterias.forEach { c ->
                    map.addMarker(MarkerOptions().position(LatLng(c.latitud, c.longitud)).title(c.nombre))
                }
            }
        }

        binding.cardPerfil.setOnClickListener { navigateTo(TAB_PERFIL) }
        binding.btnPerfilHome.setOnClickListener { navigateTo(TAB_PERFIL) }
        binding.btnFavoritos.setOnClickListener { navigateTo(TAB_FAVORITOS) }
        binding.cardComunidad.setOnClickListener { navigateTo(TAB_COMUNIDAD) }
        binding.btnBuscar.setOnClickListener { navigateTo(TAB_BUSCAR) }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), ConfiguracionActivity::class.java))
        }
        binding.btnAjustes.setOnClickListener {
            startActivity(Intent(requireContext(), ConfiguracionActivity::class.java))
        }
        binding.btnAccesibilidad.setOnClickListener {
            startActivity(Intent(requireContext(), AccesibilidadActivity::class.java))
        }
        binding.cardUsuarios.setOnClickListener {
            startActivity(Intent(requireContext(), UsuariosActivity::class.java))
        }
    }

    private fun navigateTo(tab: Int) {
        (requireActivity() as? MainContainerActivity)?.selectTab(tab)
    }

    // ── Sensor de luz ──────────────────────────────────────────────────────────

    // Se llama cada vez que el sensor detecta un cambio en la luz ambiental
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_LIGHT) return

        val lux = event.values[0]
        val prefs = requireActivity().getSharedPreferences("cafe_prefs", Context.MODE_PRIVATE)
        val darkModeActivo = prefs.getBoolean("dark_mode", false)

        // Sugerir Dark Mode solo si el ambiente está oscuro, el modo no está
        // activado y aún no se ha sugerido en esta sesión
        if (lux < LIGHT_THRESHOLD && !darkModeActivo && !darkModeSuggested) {
            darkModeSuggested = true
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                "Ambiente oscuro detectado. ¿Activar Dark Mode?",
                10000
            ).setAction("Activar") {
                // Guardar preferencia y aplicar el tema oscuro inmediatamente
                prefs.edit().putBoolean("dark_mode", true).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                requireActivity().recreate()
            }.show()
        }
    }

    // Requerido por SensorEventListener, no se utiliza en este caso
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // ── Notificaciones ────────────────────────────────────────────────────────

    private fun solicitarPermisoYNotificar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val tienePermiso = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (tienePermiso) mostrarNotificacionBienvenida()
            else notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            mostrarNotificacionBienvenida()
        }
    }

    @SuppressLint("MissingPermission")
    private fun mostrarNotificacionBienvenida() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID, "Eventos de la comunidad", NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Notificaciones sobre eventos y reuniones en cafeterías" }
            requireContext().getSystemService(NotificationManager::class.java)
                .createNotificationChannel(canal)
        }
        val notificacion = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("☕ ¡Bienvenido a Café!")
            .setContentText("Revisa los eventos de hoy en tu comunidad.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(requireContext()).notify(1, notificacion)
    }

    // ── Localización ──────────────────────────────────────────────────────────

    private fun pedirUbicacionYBuscarCafeterias() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) obtenerUbicacionYBuscar()
        else locationPermissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionYBuscar() {
        LocationServices.getFusedLocationProviderClient(requireActivity())
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null && !esEmulador(location.latitude, location.longitude))
                    buscarCafeterias(location.latitude, location.longitude)
                else buscarCafeterias(UDEM_LAT, UDEM_LNG)
            }
            .addOnFailureListener { buscarCafeterias(UDEM_LAT, UDEM_LNG) }
    }

    private fun esEmulador(lat: Double, lng: Double) = lat in 37.0..38.0 && lng in -123.0..-121.0

    private fun buscarCafeterias(lat: Double, lng: Double) {
        coffeeViewModel.buscarCafeteriasNearby(lat, lng, BuildConfig.PLACES_API_KEY)
    }

    // ── Ciclo de vida ─────────────────────────────────────────────────────────

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        // Registrar el sensor solo cuando el Fragment está visible para ahorrar batería
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        // Liberar el sensor al salir del Fragment para no consumir recursos en segundo plano
        sensorManager.unregisterListener(this)
    }

    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }

    companion object {
        const val TAB_HOME = 0
        const val TAB_BUSCAR = 1
        const val TAB_COMUNIDAD = 2
        const val TAB_FAVORITOS = 3
        const val TAB_PERFIL = 4
    }
}
